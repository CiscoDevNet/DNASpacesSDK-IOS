package com.cisco.or.sdk

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresPermission
import com.cisco.or.sdk.components.SigningView
import com.cisco.or.sdk.components.WebViewController
import com.cisco.or.sdk.enums.IdType
import com.cisco.or.sdk.enums.LoadingState
import com.cisco.or.sdk.enums.SigningState
import com.cisco.or.sdk.exceptions.*
import com.cisco.or.sdk.models.UsageStatistics
import com.cisco.or.sdk.models.UserDetail
import com.cisco.or.sdk.services.*
import com.cisco.or.sdk.types.*
import com.cisco.or.sdk.utils.Constants
import com.cisco.or.sdk.utils.SharedPrefs
import com.cisco.or.sdk.utils.Urls
import com.cisco.or.sdk.utils.Utils
import com.cisco.or.sdk.utils.ValidateUtil
import com.google.common.collect.ImmutableList
import org.json.JSONArray
import java.util.*


object OpenRoaming {
    private val TAG: String = OpenRoaming::class.java.name
    private lateinit var context: Context
    private lateinit var  sharedPref: SharedPrefs
    private var isInitialized: Boolean = false


    /**
     * Method to validate that the SDK was installed correctly and the call to its methods is working correctly.
     * This method is for testing only, there is no need for implementation.
     * return: SDK Version
     */
    fun validateSDKIsWorking(): String {
        return Constants.version
    }

    /**
     * Initializes the SDK. Checks if HotSpot2 is supported by device.
     *
     * @param context Application context.
     * @param appId The 3rd party app who is requesting access on behalf of the SDK.
     * @param initializeHandler Lambda expression to be executed, using as parameter a variable that
     * provides the last state of application (Signed, Unsigned or Signed and missing response to
     * privacy settings information)
     */
    @Throws(Hotspot2NotSupportedException::class,
        RegisterFailedException::class)
    @RequiresPermission(allOf=["android.permission.INTERNET", "android.permission.CHANGE_WIFI_STATE", "android.permission.NETWORK_SETTINGS"])
    fun registerSdk(context: Context, appId: String, dnaSpacesKey: String, initializeHandler: InitializeHandler){
        this.context = context
        this.sharedPref = SharedPrefs(context)
        sharedPref.dnaSpacesKey = dnaSpacesKey
        sharedPref.appId = appId

        try {
            val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifi.passpointConfigurations
        } catch (e: java.lang.Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw Hotspot2NotSupportedException()
        }

        try {
            if(sharedPref.sdkToken.isNullOrEmpty()) {
                RegisterService.start(context) {
                    sharedPref.sdkToken = Utils.decryptSdkToken(it!!.json!!, sharedPref.clientPrivateKey!!)
                    this.isInitialized = true
                    Log.d(TAG, "Initialized with success!")
                    initializeHandler(sharedPref.signingState)
                }
            }
            else{
                this.isInitialized = true
                Log.d(TAG, "Initialized with success!")
                initializeHandler(sharedPref.signingState)
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw RegisterFailedException()
        }
    }

    /**
     * Method called to authenticate the user and, if authentication is succesful, to associate user to device at Open Roaming backend service.
     * Authentication is handled by Open Roaming  backend service, which will provide the authentication URL to be rendered in the webview passed as parameter to the method.
     *
     * @param serviceName Which OpenRoaming identity providers should be used to identify the user.
     * (Supported values are: oneid_oauth2, google_oauth2 and apple_oauth2)
     * @param signingView Component that renders the redirected URL.
     * @param signingHandler A lambda expression that will be executed as this method execution finishes.
     * It can be used for tasks like signing-up, signing-in or performing next tasks that makes sense
     * based on the current state reported by the method. When executed, the routine will provide as parameter
     * the current state of session so that execution can continue from the same step: unsigned, signed, ToS received.
     *
     * @see SigningView
     */
    @Throws(
        NotInitializedException::class,
        TokenEmptyException::class,
        LoginFailedException::class,
        ServiceBadResponseException::class,
        Hotspot2NotSupportedException::class)
    @RequiresPermission(allOf=["android.permission.INTERNET", "android.permission.CHANGE_WIFI_STATE"])
    fun associateUser(signingView: SigningView,
                              serviceName: String,
                              signingHandler: SigningHandler){
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.UNSIGNED)
        validateIsSigned()

        val loginURL = Urls.authenticationUrl() + serviceName
        val handler = Handler(context.mainLooper)
        Utils.refreshTokenSDK(context) {
            val run = Runnable {
                kotlin.run {
                    signingView.webView.webViewClient = WebViewController(context,
                        loadingHandler = { state ->
                            signingView.progressBar.visibility =
                                if (state == LoadingState.STARTED) View.VISIBLE else View.GONE
                        },
                        finish = {
                            sharedPref.signingState = SigningState.SIGNED
                            Log.d(TAG, "Service login started.")
                            signingHandler()
                        }
                    )
                    WebViewController.configureWebView(signingView.webView)

                    val extraHeaders: MutableMap<String, String> = HashMap()
                    extraHeaders["Authorization"] = sharedPref.sdkToken!!
                    extraHeaders["Content-Type"] = "application/json"
                    signingView.webView.loadUrl(loginURL, extraHeaders)
                }
            }
            handler.post(run)
        }
    }

    private fun validateIsSigned() {
        if (sharedPref.signingState == SigningState.SIGNED){
            throw SignedException()
        }
    }

    /**
     * Sets privacy settings.
     *
     * @param acceptPrivacySettings If information will be shared (True or False)
     * @param privacySettingsHandler Lambda expression to be executed after set privacy settings
     */
    fun setPrivacySettings(acceptPrivacySettings: Boolean, privacySettingsHandler: PrivacySettingsHandler){
        Log.d(TAG, "User accepts privacy settings: $acceptPrivacySettings")
        SetPrivacySettingsService.start(context, acceptPrivacySettings) { privacySettingsResponse ->
            if (privacySettingsResponse == null) {
                throw ServiceBadResponseException()
            } else {
                Log.d(TAG, "Service signed")
                privacySettingsHandler()
            }
        }
    }

    /**
     * Download and install a profile on device
     */
    @Throws(
        NotInitializedException::class,
        ServiceBadResponseException::class,
        Hotspot2NotSupportedException::class)
    @RequiresPermission(allOf=["android.permission.INTERNET", "android.permission.CHANGE_WIFI_STATE"])
    fun installProfile(provisionProfileHandler: ProvisionProfileHandler){
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)

        try {
            InstallProfileService.start(context) { response ->
                try {
                    Utils.installProfile(context, response!!.data!!)
                    provisionProfileHandler()
                } catch (e: Exception) {
                    Log.e(TAG, Log.getStackTraceString(e))
                }
            }
        }catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw ServiceBadResponseException()
        }
    }

    /**
     *  Checks if there is a profile already provisioned on the device
     *  @param profileExistenceHandler Lambda expression to receive and handle the method return.
     *  This lambda expression receives “existence” as parameter,
     *  wich informs if a profile already exists on the device or not (True or False)
     */
    @Throws(Hotspot2NotSupportedException::class)
    @RequiresPermission(allOf=["android.permission.INTERNET", "android.permission.NETWORK_SETTINGS"])
    fun profileExistence(profileExistenceHandler: ProfileExistenceHandler) {
        try{
            val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            var existence = wifi.passpointConfigurations.size > 0
            profileExistenceHandler(existence)
        }catch (e : Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw Hotspot2NotSupportedException()
        }
    }

    /**
     * Gets the User Data
     * @param getProfileHandler lambda function with User Datas
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class
    )
    @RequiresPermission(value = "android.permission.INTERNET")
    fun getUserDetails(userDetailsHandler: UserDetailsHandler) {
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)

        try {
            UserDetailService.start(context) { response ->
                if(response?.json != null){
                    userDetailsHandler(UserDetail(response.json!!))
                }else{
                    userDetailsHandler(UserDetail("Unknown name", "", "", 0, ""))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw ServiceBadResponseException()
        }
    }

    /**
     * Gets a specific identity’s details
     *
     * @param identity One identity from user (google, apple, etc)
     * @param getDetailsIdentityHandler Lambda expression to be executed, using as parameter a variable that
     * provides the User Data data model
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class
    )
    @RequiresPermission(value = "android.permission.INTERNET")
    fun getPrivacySettings(getPrivacySettingsHandler: GetPrivacySettingsHandler) {
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)
        try {
            GetPrivacySettingsService.start(context) { response ->
                getPrivacySettingsHandler(response!!.text!!.toBoolean())
            }
        }catch (e: Exception){
            Log.e(TAG, Log.getStackTraceString(e))
            throw ServiceBadResponseException()
        }
    }

    /**
     * Delete User on backend openroaming and remove profile on device
     *
     * @param deleteUserHandler Lambda expression to be executed after delete user
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class,
        ServiceBadResponseException::class,
        TokenEmptyException::class)
    @RequiresPermission(allOf=["android.permission.INTERNET", "android.permission.NETWORK_SETTINGS"])
    fun deleteUserAccount(deleteUserHandler: DeleteUserHandler) {
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)

        try {
            DeleteUserService.start(context){ response ->
                if (response?.text!!.toBoolean()){
                    throw ServiceBadResponseException()
                }
                deleteProfileOnDevice()
                sharedPref.signingState = SigningState.UNSIGNED
                deleteUserHandler()
            }
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw ServiceBadResponseException()
        }
    }

    /**
     * Delete Profile on backend openroaming and remove profile on device
     *
     * @param serviceHandler Lambda expression to be executed after delete profile
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class,
        ServiceBadResponseException::class,
        TokenEmptyException::class)
    @RequiresPermission(allOf=["android.permission.INTERNET", "android.permission.NETWORK_SETTINGS"])
    fun deleteProfile(deleteProfileHandler: DeleteProfileHandler) {
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)

        try {
            DeleteProfileService.start(context){response ->
                if (response?.text!!.toBoolean()){
                    throw ServiceBadResponseException()
                }
                deleteProfileOnDevice()
                sharedPref.signingState = SigningState.UNSIGNED
                deleteProfileHandler()
            }
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw ServiceBadResponseException()
        }
    }

    /**
     * Delete profile on Device and clear shared preferences
     */
    @RequiresPermission(value= "android.permission.NETWORK_SETTINGS")
    private fun deleteProfileOnDevice(){
                try {
                    val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val config = wifi.passpointConfigurations
                    val fqdn = sharedPref.fqdn
                    if (config != null && config.size > 0) {
                        config.forEach({ profile ->
                            if (profile.homeSp != null && profile.homeSp.fqdn != null && profile.homeSp.fqdn == fqdn) {
                                wifi.removePasspointConfiguration(fqdn)
                            }
                        })
                    } else if (Build.VERSION.SDK_INT == 29) {
                        DummyProfileService.start(context) { response ->
                            if (response == null) {
                                throw ServiceBadResponseException()
                            }
                            Utils.installProfile(context, response.data!!)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Log.e(TAG, Log.getStackTraceString(e))
                }
            }

    @Throws(
        NotInitializedException::class,
        NotSignedException::class)
    private fun validateIfIsInitializedAnd(signingStateAtLeast: SigningState) {
        if (!this.isInitialized){
            throw NotInitializedException()
        }

        if (signingStateAtLeast.ordinal < sharedPref.signingState.ordinal){
            val e = NotSignedException()
            e.message?.let{ Log.e(TAG, it) }
            throw e
        }
    }

    /**
     * Gets usage statistics for a specific user
     * @param usageStatisticsHandler handler to convert from json to a model object.
     * @param usageStatiticsHandler Lambda expression to be executed, using as parameter a variable
     * that provides the Usage Statistics data model
     * @author Fabiana Garcia
     * @since 27/03/2020
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class)
    @RequiresPermission(value = "android.permission.INTERNET")
    fun getUsageStatistics(usageStatisticsHandler: UsageStatisticsHandler){
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)
        UsageStatisticsService.start(context){ usageStatisticsResponse ->
            if(usageStatisticsResponse == null){
                throw ServiceBadResponseException()
            }
            try{
                val data = usageStatisticsResponse.jsonArray!!
                usageStatisticsHandler(UsageStatistics(data))
            } catch(e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
                usageStatisticsHandler(UsageStatistics(JSONArray()))
            }
        }
    }

    /**
     * Gets usage statistics for a specific user
     * @param usageStatisticsHandler handler to convert from json to a model object.
     * @param usageStatiticsHandler Lambda expression to be executed, using as parameter a variable
     * that provides the Usage Statistics data model
     * @author Fabiana Garcia
     * @since 27/03/2020
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class)
    @RequiresPermission(value = "android.permission.INTERNET")
    fun getUsageStatisticsV1(usageStatisticsHandler: UsageStatisticsHandler) {
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)

        usageStatisticsHandler(UsageStatistics(JSONArray()))
    }


    /**
     * Associate the pushIdentifier to the OpenRoaming backend
     * @param pushIdentifier Push token
     * @param serviceHandler Lambda expression to be executed at the end of the method
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class)
    @RequiresPermission(value="android.permission.INTERNET")
    fun associatePushIdentifier(pushIdentifier : String, associatePushHandler: AssociatePushHandler){
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)
        PushIdentifierService.start(context, true, pushIdentifier){
            sharedPref.pushIdentifier = pushIdentifier
            associatePushHandler()
        }
    }

    /**
     * Dissociate the pushIdentifier from the OpenRoaming backend.
     * @param serviceHandler Lambda expression to be executed at the end of the method
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class)
    @RequiresPermission(value="android.permission.INTERNET")
    fun dissociatePushIdentifier(associatePushHandler: AssociatePushHandler){
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)
        PushIdentifierService.start(context, false, sharedPref.pushIdentifier!!){
            sharedPref.pushIdentifier = ""
            associatePushHandler()
        }
    }

    /**
     * Method called to associate user to device at Open Roaming backend service.
     * User is supposed to have been authenticated previously and he/she will be
     * identified in the Open Roaming backend service via oAuth token that is passed as parameter to this method.
     * @param serverAuthCode Server Authentication Code from google or apple
     * @param serviceName Which OpenRoaming identity providers should be used to identify the user.
     * (Supported values are: oneid_oauth2, google_oauth2 and apple_oauth2)
     * @param signingHandler Lambda expression to be executed at the end of the method
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class)
    @RequiresPermission(value="android.permission.INTERNET")
    fun associateUser(serverAuthCode: String, signingServiceName: String, signingHandler: SigningHandler) {
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.UNSIGNED)
        validateIsSigned()
        ServerAuthCodeService.start(context, serverAuthCode, signingServiceName) {
            sharedPref.signingState = SigningState.SIGNED
            signingHandler()
        }
    }

    /**
     * Dissociate the pushIdentifier from the OpenRoaming backend.
     * @param signingHandler Lambda expression to be executed at the end of the method
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class)
    @RequiresPermission(value="android.permission.INTERNET")
    fun associateUser(idType: IdType, id: String, signingHandler: SigningHandler){
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.UNSIGNED)
        validateIsSigned()
        when (idType) {
            IdType.EMAIL -> if (!ValidateUtil.validateEmail(id)) throw EmailException()
        }
        try {
            AssociateUserService.start(context, idType, id){
                sharedPref.signingState = SigningState.SIGNED
                signingHandler()
            }
        }catch (e: Exception){
            Log.e(TAG, Log.getStackTraceString(e))
            throw ServiceBadResponseException()
        }
    }

    /**
     * Updates user on the Openroaming backend
     * @param userDetail UserDetail Data Model
     * @param serviceHandler Lambda expression to be executed at the end of the method
     * @see UserDetail
     *
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class)
    @RequiresPermission(value="android.permission.INTERNET")
    fun updateUserDetails(userDetail: UserDetail, updateUserHandler: UpdateUserHandler){
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)
        val handler = Handler(context.mainLooper)
        try {
            var user = UserDetail("Unknown name", "", "", 0, "")
            UserDetailService.start(context) { response ->
                val run = Runnable {
                    kotlin.run {
                        if (response?.json != null) {
                            user = UserDetail(response.json!!)
                        }
                        if (user.email.isNullOrEmpty()) {
                            throw EmailException()
                        } else {
                            UpdateUserService.start(context, userDetail, user.email) {
                                updateUserHandler()
                            }
                        }
                    }
                }
                handler.post(run)
            }
        }catch (e: Exception){
            Log.e(TAG, Log.getStackTraceString(e))
            throw ServiceBadResponseException()
        }
    }

    /**
     * Gets the user's location
     * @param LocationHandler : location- Provides the Pair of location. First value equal to X and second value equal to Y
     */
    @Throws(
        NotInitializedException::class,
        NotSignedException::class)
    fun getLocation(locationHandler: LocationHandler) {
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.SIGNED)
        try {
            UserLocationService.start(context) { response ->
                if (response?.json != null) {
                    val json = response.json!!
                    val x = json.getDouble("x")
                    val y = json.getDouble("y")
                    locationHandler(Pair(x, y))
                } else {
                    locationHandler(Pair(null, null))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw ServiceBadResponseException()
        }
    }

    /**
     * Handle push notification and perform OpenRoaming functions if necessary.
     * Whenever a push notification is received, the application must call this method in order to check if this is an Open Roaming system message or not.
     * If this is an OR system message, the method will handle it and return an indication that the application should disregard the message:  disregardNotification
     * parameter passed to notificationHandler handler equal to True.
     * If this is not an OR system message, the method will ignore it and return an indication that the application must handle the message itself:  disregardNotification
     * parameter passed to notificationHandler equal to False.  In this case,  notificationHandler will also receive as a parameter the message to be handled by application.
     * @param message Message from push notification
     * @param notificationHandler As a result of the method, the following information is provided as parameter to be used by lambdas expressions notificationHandler:
     * parameter: disregardNotification - Provides if the message is of interest to OpenRoaming (True/False)
     * parameter: message (optional) - Provides the message notification push. message will only have value, if  disregardNotification has the value “False”.
     */
    @Throws(
        NotInitializedException::class,
        PushNotificationNotAssociatedException::class,
        Hotspot2NotSupportedException::class)
    @RequiresPermission(allOf=["android.permission.INTERNET", "android.permission.NETWORK_SETTINGS"])
    fun handleNotification(message: String, notificationHandler: NotificationHandler){
        validateIfIsInitializedAnd(signingStateAtLeast = SigningState.UNSIGNED)
        if(!isActiveAssociatePush()){
            throw PushNotificationNotAssociatedException()
        }
        try {
            val openRoamingInterest = ImmutableList.of("<OR - install profile>","<OR - delete profile>")
            when {
                message.contains(openRoamingInterest[0]) -> {
                    val data = message.replace(openRoamingInterest[0]+"\n","").toByteArray(Charsets.UTF_8)
                    Utils.installProfile(context, data)
                    notificationHandler(true, null)
                }
                message.contains(openRoamingInterest[1]) -> {
                    deleteProfileOnDevice()
                    notificationHandler(true, null)
                }
                else -> {
                    notificationHandler(false, message)
                }
            }
        }catch (e: Exception){
            Log.e(TAG, Log.getStackTraceString(e))
            throw ServiceBadResponseException()
        }
    }

    fun isActiveAssociatePush(): Boolean{
        return !sharedPref.pushIdentifier.isNullOrEmpty()
    }

    private fun signedState(state: SigningState){
        sharedPref.signingState = state
    }
}
