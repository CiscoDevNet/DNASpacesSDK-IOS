package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls
import com.cisco.or.sdk.utils.Utils
import org.json.JSONObject
import kotlin.collections.ArrayList

/**
 * Only OAuth Token
 */
internal object ServerAuthCodeService : Service() {
    private val TAG: String = ServerAuthCodeService::class.java.name

    private var oAuthToken : String = ""
    private var serviceName : String = ""

    /**
     * Not implemented.
     * Use start(context: Context, oAuthToken: String, serviceName: SigningServiceName, serviceHandler: ServiceHandler).
     */
    override fun start(context: Context, serviceHandler: ServiceHandler) {
        if(Utils.internalCall(Service::class.qualifiedName.toString(), "start")){
            super.start(context,serviceHandler)
        }else{
            throw NotImplementedError()
        }
    }

    fun start(context: Context, oAuthToken: String, serviceName: String, serviceHandler: ServiceHandler) {
        this.oAuthToken = oAuthToken
        this.serviceName = serviceName
        super.start(context, serviceHandler)
    }

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val body = JSONObject()
        body.put("authType", this.serviceName)
        body.put("id", this.oAuthToken)

        val url = Urls.oauthTokenUrl()
        val requestData = RequestData(url, headerParams, HTTP_METHOD.POST, body.toString())
        call(context, requestData, RESPONSE_FORMATTER.TEXT, serviceHandler)
    }
}