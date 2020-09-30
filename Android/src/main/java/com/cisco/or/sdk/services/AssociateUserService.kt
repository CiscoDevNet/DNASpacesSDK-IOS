package com.cisco.or.sdk.services

import android.content.Context
import com.cisco.or.sdk.enums.IdType
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls
import com.cisco.or.sdk.utils.Utils
import org.json.JSONObject
import kotlin.collections.ArrayList

/**
 * Associate User on openroaming backend
 */
internal object AssociateUserService : Service() {
    private val TAG: String = AssociateUserService::class.java.name

    private var idType : IdType = IdType.EMAIL
    private var id : String  = ""

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

    fun start(context: Context, idType: IdType, id : String, serviceHandler: ServiceHandler) {
        this.idType = idType
        this.id = id
        super.start(context, serviceHandler)
    }

    override fun onStart(context: Context, headerParams: ArrayList<HeaderParam>, serviceHandler: ServiceHandler) {
        val body = JSONObject()
        body.put("idType", this.idType.value)
        body.put("id", this.id)

        val url = Urls.associateUserUrl()
        val requestData = RequestData(url, headerParams, HTTP_METHOD.POST, body.toString())
        call(context, requestData, RESPONSE_FORMATTER.TEXT, serviceHandler)
    }
}