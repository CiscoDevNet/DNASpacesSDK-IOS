package com.cisco.or.sdk.utils


internal object Urls {
    private const val BASE_URL = "https://dev-api.openroaming.net/"
    private const val DUMMY_PROFILE_URL = "api/profiles/v1/user/dummypasspoint/tmp.xml"

    fun getDummyProfileUrl() = BASE_URL + DUMMY_PROFILE_URL

    //Version 2.0 URLS
    private const val API_URL = "https://dev-api.openroaming.net"
    fun registerUrl() = "$API_URL/sdk/v1/register"
    fun refreshTokenUrl() = "$API_URL/sdk/v1/refreshsdktoken"
    fun associatePush() = "$API_URL/sdk/v1/associatepush"
    fun updateUserUrl() = "$API_URL/sdk/v1/user"
    fun oauthTokenUrl() = "$API_URL/sdk/v1/authenticate/authtoken"
    fun userDetailsUrl() = "$API_URL/sdk/v1/user"
    fun deleteProfileUrl() = "$API_URL/sdk/v1/profile"
    fun deleteUserUrl() = "$API_URL/sdk/v1/user"
    fun userLocationUrl() = "$API_URL/sdk/v1/getlocation"
    fun associateUserUrl() = "$API_URL/sdk/v1/associateuser"
    fun installProfileUrl() = "$API_URL/sdk/v1/profile"
    fun authenticationUrl() = "$API_URL/sdk/v1/authenticate/authtoken?authType="
    fun usageStatisticsUrl() = "$API_URL/sdk/v1/user/stats"
    fun privacySettingsUrl() = "$API_URL/sdk/v1/user/anonymousemail"

}