package com.cisco.or.sdk.services

import android.content.Context
import android.util.Base64
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Constants
import com.cisco.or.sdk.utils.CryptoUtil
import com.cisco.or.sdk.utils.SharedPrefs
import com.cisco.or.sdk.utils.Urls
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Register the app on the API.
 */
internal object RegisterService : Service(){

    override fun start(context: Context, serviceHandler: ServiceHandler) {
        val sharedPrefs = SharedPrefs(context)
        if(sharedPrefs.clientPublicKey.isNullOrEmpty()){
            generateKeys(sharedPrefs)
        }

        val headerParams = ArrayList<HeaderParam>()
        headerParams.add(HeaderParam("Content-Type", "application/json"))
        headerParams.add(HeaderParam("x-dnaspaces-apikey", SharedPrefs(context).dnaSpacesKey!!))
        headerParams.add(HeaderParam("Authorization", CryptoUtil.encryptPublicKey(sharedPrefs.clientPublicKey!!, Constants.SERVER_PUBLIC_KEY)))

        onStart(context, headerParams, serviceHandler)
    }

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val url = Urls.registerUrl()

        val json = JSONObject()
        json.put("guid", UUID.randomUUID().toString())
        json.put("appId", SharedPrefs(context).appId)
        json.put("platform", "android")

        val requestData = RequestData(url, headerParams, HTTP_METHOD.POST, json.toString())
        call(context, requestData, RESPONSE_FORMATTER.JSON, serviceHandler)
    }

    private fun generateKeys(sharedPrefs: SharedPrefs){
        val keys = CryptoUtil.generateKeyPair()
        sharedPrefs.clientPublicKey = Base64.encodeToString(keys.public.encoded, Base64.NO_PADDING)
        sharedPrefs.clientPrivateKey = Base64.encodeToString(keys.private.encoded, Base64.NO_PADDING)
    }
}