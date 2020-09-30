package com.cisco.or.sdk.models

import org.json.JSONObject

class UserData internal constructor(json: JSONObject, val privacySettings: Boolean) {
    var name: String? = try { json.getString("name") } catch (e: Exception) { "" }
    var email: String? = try { json.getString("email") } catch (e: Exception) { "" }
    var phone: String? = try { json.getString("phone") } catch (e: Exception) { "" }
    var age: String? = try { json.getString("age") } catch (e: Exception) { "" }
    var zipCode: String? = try { json.getString("zip") } catch (e: Exception) { "" }


}