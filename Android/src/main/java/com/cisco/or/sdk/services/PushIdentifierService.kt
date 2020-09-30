package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls
import com.cisco.or.sdk.utils.Utils
import org.json.JSONObject
import kotlin.collections.ArrayList

/**
 * Associate/Deassociate push identifier
 */
internal object PushIdentifierService : Service() {
    private val TAG: String = PushIdentifierService::class.java.name

    private var optPush : Boolean = true
    private var pushIdentifier : String  = ""

    /**
     * Not implemented.
     * Use start(context: Context, acceptPrivacySettings: Boolean, serviceHandler: ServiceHandler).
     */
    override fun start(context: Context, serviceHandler: ServiceHandler) {
        if(Utils.internalCall(Service::class.qualifiedName.toString(), "start")){
            super.start(context, serviceHandler)
        }else{
            throw NotImplementedError()
        }
    }

    fun start(context: Context, optPush: Boolean, pushIdentifier: String, serviceHandler: ServiceHandler) {
        this.optPush = optPush
        this.pushIdentifier = pushIdentifier
        super.start(context, serviceHandler)
    }

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val body = JSONObject()
        body.put("optIn", this.optPush)
        body.put("pushIdentifier", this.pushIdentifier)

        val url = Urls.associatePush()
        val requestData = RequestData(url, headerParams, HTTP_METHOD.POST, body.toString())
        call(context, requestData, RESPONSE_FORMATTER.TEXT, serviceHandler)
    }
}