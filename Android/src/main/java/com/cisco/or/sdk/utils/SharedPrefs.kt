package com.cisco.or.sdk.utils

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.enums.SigningState
import java.security.Key
import java.security.KeyPair
import java.security.PublicKey

internal class SharedPrefs(context:Context) {

    companion object{
        private const val PREF_SDK_TOKEN = "PREF_SDK_TOKEN"
        private const val PREF_SIGNING_STATE = "PREF_SIGNING_STATE"
        private const val FQDN = "INSTALLED_FQDN"
        private const val PUSH_IDENTIFIER = "PREF_PUSH_IDENTIFIER"
        private const val PREF_DNA_SPACES_KEY = "PREF_DNA_SPACES_KEY"
        private const val CLIENT_PUBLIC_KEY = "PREF_CLIENT_PUBLIC_KEY"
        private const val CLIENT_PRIVATE_KEY = "PREF_CLIENT_PRIVATE_KEY"
        private const val APP_ID = "APP_ID"
    }

    private val sharedPref: SharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    var clientPublicKey:String?
        get() = sharedPref.getString(CLIENT_PUBLIC_KEY, null)
        set(value) = sharedPref.edit().putString(CLIENT_PUBLIC_KEY, value).apply()
    var clientPrivateKey:String?
        get() = sharedPref.getString(CLIENT_PRIVATE_KEY, null)
        set(value) = sharedPref.edit().putString(CLIENT_PRIVATE_KEY, value).apply()

    var appId:String?
        get() = sharedPref.getString(APP_ID, null)
        set(value) = sharedPref.edit().putString(APP_ID, value).apply()

    var dnaSpacesKey:String?
        get() = sharedPref.getString(PREF_DNA_SPACES_KEY, null)
        set(value) = sharedPref.edit().putString(PREF_DNA_SPACES_KEY, value).apply()

    var sdkToken:String?
        get() = sharedPref.getString(PREF_SDK_TOKEN, null)
        set(value) = sharedPref.edit().putString(PREF_SDK_TOKEN, value).apply()

    var signingState:SigningState
    get() = when (sharedPref.getInt(PREF_SIGNING_STATE, SigningState.UNSIGNED.ordinal)){
        SigningState.SIGNED.ordinal -> SigningState.SIGNED
        else -> SigningState.UNSIGNED
    }
    set(value) = sharedPref.edit().putInt(PREF_SIGNING_STATE, value.ordinal).apply()

    var fqdn:String?
        get() = sharedPref.getString(FQDN, "")
        set(value) = sharedPref.edit().putString(FQDN, value).apply()


    var pushIdentifier:String?
        get() = sharedPref.getString(PUSH_IDENTIFIER, "")
        set(value) = sharedPref.edit().putString(PUSH_IDENTIFIER, value).apply()

}