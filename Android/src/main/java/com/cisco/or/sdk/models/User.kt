package com.cisco.or.sdk.models

import org.json.JSONObject

class User internal constructor(json: JSONObject, privacySettings: Boolean) {
    private val oauthIdsList:ArrayList<String>
    val oauthIds:Array<String> get() = oauthIdsList.toTypedArray()
    val userData:UserData

    init {
        var userId:String = json.getString("user_id")
        val oauthJSON = json.getJSONArray("oauth_ids")
        oauthIdsList = ArrayList()
        for (i in 0 until oauthJSON.length()){
            oauthIdsList.add(oauthJSON[i].toString())
        }
        userData = UserData(json.getJSONObject("userdata"), privacySettings)
    }
}