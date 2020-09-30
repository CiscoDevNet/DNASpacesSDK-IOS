package com.cisco.or.sdk.models

import com.cisco.or.sdk.exceptions.EmailException
import com.cisco.or.sdk.exceptions.PhoneNumberException
import com.cisco.or.sdk.utils.ValidateUtil
import org.json.JSONObject
import java.lang.Exception
import java.util.*

data class UserDetail(val name: String, val phone: String, val email: String, val age: Int, val zipCode: String ) {

    init {
        if(phone.isNotEmpty() && !ValidateUtil.validatePhoneNumber(phone)){
            throw PhoneNumberException()
        }
        if(email.isNotEmpty() && !ValidateUtil.validateEmail(email)){
            throw EmailException()
        }
    }

}

fun UserDetail(jsonObject: JSONObject): UserDetail{
    val name = try { jsonObject.getString("name") }catch (e:Exception) {""}
    val phone = try { jsonObject.getString("phone") }catch (e:Exception) {""}
    val email = try { jsonObject.getString("email") }catch (e:Exception) {""}
    val age = try { jsonObject.getInt("age") }catch (e:Exception) {0}
    val zip = try { jsonObject.getString("zip") }catch (e:Exception) {""}
    return UserDetail(name, phone, email, age, zip)
}