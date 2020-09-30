package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.CryptoUtilAES
import com.cisco.or.sdk.utils.SharedPrefs
import com.cisco.or.sdk.utils.Urls

/**
 * Refresh the sdk token on the API.
 */
internal object RefreshTokenService : Service(){

    override fun start(context: Context, serviceHandler: ServiceHandler) {
        onStart(context, buildHeaderParams(context), serviceHandler)
    }

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val url = Urls.refreshTokenUrl()

        val requestData = RequestData(url, headerParams, HTTP_METHOD.GET, null)
        call(context, requestData, RESPONSE_FORMATTER.JSON, serviceHandler)
    }
}