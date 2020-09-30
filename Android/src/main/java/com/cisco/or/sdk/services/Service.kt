package com.cisco.or.sdk.services

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.provider.Settings
import com.cisco.or.sdk.exceptions.ServiceBadResponseException
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.SharedPrefs
import com.cisco.or.sdk.utils.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors

internal abstract class Service {

    open fun start(context: Context, serviceHandler: ServiceHandler) {
        Utils.refreshTokenSDK(context){
            onStart(context, buildHeaderParams(context), serviceHandler)
        }
    }

    /**
     * Creates a HeaderParam list with
     * Content-Type: application/json
     * Authorization: <SDK Token encrypted with the server public key>
     */
    fun buildHeaderParams(context: Context): ArrayList<HeaderParam> {
        val sharedPrefs = SharedPrefs(context)
        val list = ArrayList<HeaderParam>()
        list.add(HeaderParam("Content-Type", "application/json"))
        list.add(HeaderParam("Authorization", sharedPrefs.sdkToken!!))
        return list
    }

    /**
     * This method should not be called directly. Call start().
     */
    protected abstract fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler)

    @Throws(ServiceBadResponseException::class)
    fun call(context: Context, requestData: RequestData, responseFormatter: RESPONSE_FORMATTER, serviceHandler: ServiceHandler){
        doRestCall(context, requestData, responseFormatter) { _, response: HTTPResponse?, _ ->
            when {
                response == null -> throw ServiceBadResponseException()
                response.status != 200 -> throw ServiceBadResponseException()
                else -> serviceHandler(response)
            }
        }
    }

    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.isNullOrBlank() || manufacturer.isNullOrBlank()) {
            "Undefined Model"
        } else if (model.startsWith(manufacturer)) {
            model
        } else {
            "$manufacturer $model"
        }
    }

    fun bluetoothName(context: Context): String {
        val bluetooth  = Settings.Secure.getString(context.contentResolver, "bluetooth_name")
        return if(bluetooth.isNullOrBlank())
            "Undefined Bluetooth"
        else
            bluetooth
    }

    fun androidId(context: Context): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return if(androidId.isNullOrBlank())
            "Undefined Android ID"
        else
            androidId
    }

    class RequestData(
        val url: String,
        val headerParams: ArrayList<HeaderParam>,
        val method: HTTP_METHOD,
        val body: String?)

    enum class HTTP_METHOD {
        GET, PUT, POST, DELETE, OPTIONS
    }

    enum class RESPONSE_FORMATTER {
        TEXT, JSON, BINARY, JSONArray
    }

    @Throws(IOException::class)
    private fun doRestCall(context: Context,
                   requestData: RequestData,
                   responseFormatter: RESPONSE_FORMATTER,
                   callback: CallbackFunction) {

        val task = RestCallWorker()
        val params = RestParams(context, requestData, responseFormatter, callback)
        task.execute(params)
    }

    private class RestParams(
        val context: Context,
        val requestData: RequestData,
        val responseFormatter: RESPONSE_FORMATTER,
        val callback: CallbackFunction)

    private class RestCallWorker : AsyncTask<RestParams, Any?, Any?>() {
        override fun doInBackground(vararg params: RestParams): Void? {
            val p = params[0]
            try {
                val response = Worker.doRestCall(p.requestData, p.responseFormatter)
                p.callback(p.context, response, null)
            } catch (e: IOException) {
                p.callback(p.context, null, e)
            }
            return null
        }
    }

    private object Worker {

        fun doRestCall(requestData: RequestData, responseFormatter: RESPONSE_FORMATTER): HTTPResponse {
            val response = HTTPResponse()
            val connection = URL(requestData.url).openConnection() as HttpURLConnection
            connection.requestMethod = requestData.method.name

            requestData.headerParams.forEach { header ->
                connection.setRequestProperty(header.name, header.value)
            }
            requestData.body?.let { body ->
                val outputInBytes: ByteArray = body.toByteArray()
                val os: OutputStream = connection.outputStream
                os.write(outputInBytes)
                os.close()
            }

            response.status = connection.responseCode

            val input = connection.content as InputStream
            if (responseFormatter == RESPONSE_FORMATTER.BINARY) {
                val output = ByteArrayOutputStream()
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    output.write(data, 0, count)
                }
                input.close()
                response.data = output.toByteArray()
                output.close()
            } else {
                val reader = BufferedReader(InputStreamReader(input))
                response.text = reader.lines().collect(Collectors.joining())
                reader.close()
            }
            when (responseFormatter) {
                RESPONSE_FORMATTER.JSON -> try {
                    response.json = JSONObject(response.text!!)
                } catch (exp: JSONException) {
                    response.jsonException = exp
                }
                RESPONSE_FORMATTER.JSONArray -> try {
                    response.jsonArray = JSONArray(response.text)
                } catch (exp: JSONException) {
                    response.jsonException = exp
                }
                else -> {
                }
            }
            return response
        }
    }

    inline fun <reified T> tryCast(instance: Any?):T? {
        return if (instance is T) {
            instance
        } else{
            null
        }
    }

    class HeaderParam(val name: String, val value: String)
}

class HTTPResponse {
    var text: String? = null
    var data: ByteArray? = null
    var status = -1
    var json: JSONObject? = null
    var jsonArray: JSONArray? = null
    var jsonException: JSONException? = null
}

internal typealias CallbackFunction = (context: Context?, response: HTTPResponse?, e: IOException?) -> Unit