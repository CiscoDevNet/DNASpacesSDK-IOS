package com.cisco.or.sdk.mock

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.util.Base64
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.mockk.every
import io.mockk.mockkStatic
import java.util.*


fun mockFunctions(sharedPreferences: SharedPreferences, context: Context, accessToken : String) {
    val token = generateJWTToken()
    every {sharedPreferences.getString("PREF_ACCESS_TOKEN", null)} returns (token)
    every {sharedPreferences.getString("PREF_SDK_TOKEN", null)} returns (token)
    every {sharedPreferences.edit().putString("PREF_SDK_TOKEN", token).apply()} returns Unit
    every {sharedPreferences.getString("PREF_SECRET_KEY", null)} returns ("662ede816988e58fb6d057d9d85605e0")
    every {context.getSharedPreferences("ACCESS_TOKEN", Context.MODE_PRIVATE)} returns (sharedPreferences)
    every {context.applicationInfo } returns ApplicationInfo()
    mockkStatic(Base64::class)
    every { Base64.encodeToString(any(), any()) } returns "encoded"
}

fun generateJWTToken() : String {
    val accessKey = "auzNN7V0aB30poSilNi15HCiE"

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, +1)

    return JWT.create()
        .withSubject("Token")
        .withExpiresAt(calendar.time)
        .sign(Algorithm.HMAC256(accessKey))
}


