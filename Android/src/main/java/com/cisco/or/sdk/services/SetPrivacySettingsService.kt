package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls
import com.cisco.or.sdk.utils.Utils

/**
 * Accepts or not to share user e-mail
 */
internal object SetPrivacySettingsService : Service(){
    private var acceptPrivacySettings: Boolean = false

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

    /**
     * Sends the accept share email answer.
     * @param context Context of the functionality
     * @param acceptPrivacySettings True = accept privacy settings
     * @param serviceHandler Type of handler to response
     */
    fun start(context: Context, acceptPrivacySettings: Boolean, serviceHandler: ServiceHandler) {
        this.acceptPrivacySettings = acceptPrivacySettings
        super.start(context,serviceHandler)
    }

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val url = Urls.privacySettingsUrl() + "/" + acceptPrivacySettings
        val requestData = RequestData(url, headerParams, HTTP_METHOD.PUT, null)
        call(context, requestData, RESPONSE_FORMATTER.TEXT, serviceHandler)
    }
}