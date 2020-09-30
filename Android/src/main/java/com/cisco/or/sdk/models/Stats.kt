package com.cisco.or.sdk.models

import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList


class UsageStatistics internal constructor(array: JSONArray) {
    private val statsList: ArrayList<Stats>
    val usageStatistics: ArrayList<Stats> get() = statsList

    init {
        statsList = ArrayList()
        for (i in 0 until array.length()) {
            statsList.add(Stats(array.getJSONObject(i)))
        }
    }

    class Stats internal constructor(json: JSONObject) {
        val ssidDevice: String = json.getString("ssid")
        val deviceUsed: String = json.getString("device")
        val durationTime: Int = json.getInt("duration")
        val dateTime: String = json.getString("datetime")
        val starttime: Long = json.getLong("starttime")
        val endtime: Long = json.getLong("endtime")
    }
}
