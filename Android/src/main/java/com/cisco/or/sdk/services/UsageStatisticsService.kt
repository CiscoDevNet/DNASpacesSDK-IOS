package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls

/**
 * Gets a device's usage statistics
 */

internal object UsageStatisticsService : Service(){
    /**
     * Method responsible to get a device's usage statistics
     *
     * @param context Context of the functionality.
     * @param sdkToken User's access token.
     * @param serviceHandler Type of handler to response.
     * @author Fabiana Garcia
     * @since 24/03/2020
     */
    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val url = Urls.usageStatisticsUrl()
        val requestData = RequestData(url, headerParams, HTTP_METHOD.GET, null)
        call(context, requestData, RESPONSE_FORMATTER.JSONArray, serviceHandler)
    }
}