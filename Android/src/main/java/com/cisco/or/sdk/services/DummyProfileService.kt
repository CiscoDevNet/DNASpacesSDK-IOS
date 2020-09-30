package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.SharedPrefs
import com.cisco.or.sdk.utils.Urls
import com.cisco.or.sdk.utils.Utils

/**
 * Accepts or not to share user e-mail
 */
internal object DummyProfileService : Service(){

    override fun start(context: Context, serviceHandler: ServiceHandler) {
        onStart(context, buildHeaderParams(context), serviceHandler)
    }

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val url = Urls.getDummyProfileUrl()
        val requestData = RequestData(url, headerParams, HTTP_METHOD.GET, null)
        call(context, requestData, RESPONSE_FORMATTER.BINARY, serviceHandler)
    }
}