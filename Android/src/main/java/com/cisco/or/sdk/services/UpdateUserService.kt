package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.models.UserDetail
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls
import com.cisco.or.sdk.utils.Utils
import org.json.JSONObject
import kotlin.collections.ArrayList

/**
 * Update User
 */
internal object UpdateUserService : Service() {
    private val TAG: String = UpdateUserService::class.java.name

    private lateinit var userDetail : UserDetail
    private lateinit var email : String

    /**
     * Not implemented.
     * Use start(context: Context, user: User, serviceHandler: ServiceHandler).
     */
    override fun start(context: Context, serviceHandler: ServiceHandler) {
        if(Utils.internalCall(Service::class.qualifiedName.toString(), "start")){
            super.start(context, serviceHandler)
        }else{
            throw NotImplementedError()
        }
    }

    fun start(context: Context, userDetail: UserDetail, email: String, serviceHandler: ServiceHandler) {
        this.userDetail = userDetail
        this.email = email
        super.start(context, serviceHandler)
    }

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val body = JSONObject()
        body.put("name", this.userDetail.name)
        body.put("phone", this.userDetail.phone)
        body.put("email", this.email)
        body.put("age", this.userDetail.age)
        body.put("zip", this.userDetail.zipCode)

        val url = Urls.updateUserUrl()
        val requestData = RequestData(url, headerParams, HTTP_METHOD.POST, body.toString())
        call(context, requestData, RESPONSE_FORMATTER.JSON, serviceHandler)
    }
}