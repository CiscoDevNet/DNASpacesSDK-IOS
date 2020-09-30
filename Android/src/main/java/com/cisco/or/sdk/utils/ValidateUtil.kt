package com.cisco.or.sdk.utils

import android.util.Patterns

internal object ValidateUtil {

    fun validatePhoneNumber(phone : String): Boolean{
        return !phone.isNullOrEmpty() && Patterns.PHONE.matcher(phone).matches()
    }

    fun validateEmail(email : String): Boolean{
        return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}