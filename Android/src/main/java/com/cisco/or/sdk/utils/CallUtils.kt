package com.cisco.or.sdk.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.hotspot2.ConfigParser
import android.net.wifi.hotspot2.PasspointConfiguration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.auth0.android.jwt.JWT
import com.cisco.or.sdk.OpenRoaming
import com.cisco.or.sdk.exceptions.*
import com.cisco.or.sdk.services.RefreshTokenService
import com.cisco.or.sdk.types.RefreshTokenHandler
import org.json.JSONObject

internal object Utils {

    /**
     * Method to filter the caller from start
     * @return(Boolean): Return true if the start has been called by the class itself, if it is from an external class it returns false
     */
    fun internalCall(className: String, methodName: String): Boolean {
        Thread.currentThread().stackTrace.forEach { element: StackTraceElement ->
            if (element.methodName == methodName && element.className == className)
                return true
        }
        return false
    }

    /**
     * Method to install a profile on device
     * @param context: Application context to install a profile
     * @param data: profile from response of provisioning profile
     */
    fun installProfile(context: Context, data: ByteArray) {
        try {
            if (data.isEmpty()) {
                throw EmptyProfileException()
            }
            val actualConfig = ConfigParser.parsePasspointConfig(Constants.MimeType, data)
            val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifi.addOrUpdatePasspointConfiguration(actualConfig)
            SharedPrefs(context).fqdn = actualConfig.homeSp.fqdn

        } catch (e: Exception) {
            throw Hotspot2NotSupportedException()
        }
    }

    /**
     * Method to return a SDK Token decrypted
     * @param value JsonObject from register or refresh API
     * @param privateKey CLIENT private key, generated before call Register API
     * @return SDKTOKEN to be decode with JWT
     */
    fun decryptSdkToken(value: JSONObject, privateKey: String): String {
        val sdkTokenEncrypted = value.getString("sdkToken")
        val secretKeyEncrypted = value.getString("key")
        if (sdkTokenEncrypted.isNullOrEmpty() || secretKeyEncrypted.isNullOrEmpty()) {
            throw RegisterFailedException()
        }
        val secretKey = CryptoUtil.decrypt(secretKeyEncrypted, privateKey)
        val sdkToken = CryptoUtilAES.decrypt(sdkTokenEncrypted, secretKey)
        return sdkToken
    }

    fun refreshTokenSDK(context: Context, refreshTokenHandler: RefreshTokenHandler) {
        val sharedPref = SharedPrefs(context)
        val token: String? = sharedPref.sdkToken
        if (token.isNullOrBlank()) {
            throw TokenEmptyException()
        } else {
            val jwt = JWT(token)
            if (!jwt.isExpired(0)) {
                refreshTokenHandler()
            } else {
                RefreshTokenService.start(context) {
                    sharedPref.sdkToken = decryptSdkToken(it!!.json!!, sharedPref.clientPrivateKey!!)
                    refreshTokenHandler()
                }
            }
        }
    }
}