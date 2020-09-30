package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.enums.IdType
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls
import com.cisco.or.sdk.utils.Utils
import kotlin.collections.ArrayList

/**
 * Get User details on openroaming backend
 */
internal object UserDetailService : Service() {
    private val TAG: String = UserDetailService::class.java.name

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val url = Urls.userDetailsUrl()
        val requestData = RequestData(url, headerParams, HTTP_METHOD.GET, null)
        call(context, requestData, RESPONSE_FORMATTER.JSON, serviceHandler)
    }
}