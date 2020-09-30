package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls
import kotlin.collections.ArrayList

/**
 * Download Profile OpenRoaming
 */
internal object InstallProfileService : Service() {
    private val TAG: String = InstallProfileService::class.java.name

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val url = Urls.installProfileUrl()
        val requestData = RequestData(url, headerParams, HTTP_METHOD.GET, null)
        call(context, requestData, RESPONSE_FORMATTER.BINARY, serviceHandler)
    }
}