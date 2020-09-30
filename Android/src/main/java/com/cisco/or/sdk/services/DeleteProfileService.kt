package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.enums.IdType
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls
import com.cisco.or.sdk.utils.Utils
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Delete Profile on openroaming backend
 */
internal object DeleteProfileService : Service() {
    private val TAG: String = DeleteProfileService::class.java.name

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {

        val url = Urls.deleteProfileUrl()
        val requestData = RequestData(url, headerParams, HTTP_METHOD.DELETE, null)
        call(context, requestData, RESPONSE_FORMATTER.TEXT, serviceHandler)
    }
}