package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls
import kotlin.collections.ArrayList

/**
 * Get User location on openroaming backend
 */
internal object UserLocationService : Service() {
    private val TAG: String = UserLocationService::class.java.name

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val url = Urls.userLocationUrl()
        val requestData = RequestData(url, headerParams, HTTP_METHOD.GET, null)
        call(context, requestData, RESPONSE_FORMATTER.JSON, serviceHandler)
    }
}