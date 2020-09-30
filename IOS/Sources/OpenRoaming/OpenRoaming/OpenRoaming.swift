//
//  OpenRoaming.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 10/04/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

public class OpenRoaming {
    
    public static let obj = OpenRoaming()
    let TAG: String = "OpenRoaming"
    let sharedPref: SharedPrefs

    private init()
    {
        self.sharedPref = SharedPrefs()
    }
    
    private func showVersion() -> String {
        let nsObject: AnyObject? = Bundle.main.infoDictionary!["CFBundleShortVersionString"] as AnyObject?
        return nsObject as! String
    }
    
    private func registerSdk(appId: String, dnaSpacesKey: String, registerSdkHandler: @escaping RegisterSdkHandler) {
        self.sharedPref.appId = appId
        self.sharedPref.dnaSpacesKey = dnaSpacesKey
        
        if isSdkRegistered() {
            registerSdkHandler(self.sharedPref.signingState, nil)
            return
        }
        
        let service = RegisterService()
        service.start(handler: registerSdkHandler)
    }
    
    private func isSdkRegistered() -> Bool{
        let token = sharedPref.sdkToken ?? ""
        return  !token.isEmpty
    }
    
    private func refreshToken(handler: @escaping (OpenRoamingError?) -> Void){
        let token = JWTDecoder(sharedPref.sdkToken ?? "")
        if !token.expired() {
            handler(nil)
        }
        else {
            let service  = RefreshTokenService()
            service.start(handler: { _, error in
                handler(error)
            })
        }
    }
    
    private func associateUser(signingView: SigningView, serviceName: String, signingHandler: @escaping SigningHandler) {
        
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.unsigned) {
            signingHandler(error)
            return
        }

        refreshToken(handler: {
            error in
            if error != nil {
                signingHandler(error)
            }
            else {
                self.loadSigningView(signingView: signingView, serviceName: serviceName, signingHandler: signingHandler)
            }
        })
    }
    
    private func loadSigningView(signingView: SigningView, serviceName: String, signingHandler: @escaping SigningHandler) {
        let urlString: String = Urls.authenticationUrl(serviceName: serviceName)
        signingView.load(urlString: urlString, signingView: signingView, sharedPrefs: sharedPref, signingHandler: { error in
            if (error != nil) {
                signingHandler(error)
            }
            else{
                self.sharedPref.signingState = SigningState.signed
                signingHandler(nil)
            }
        })
    }
    
    private func installProfile(provisionProfileHandler: @escaping ProvisionProfileHandler) {
        let service = GetProfileService()
        service.start(serviceHandler: { data, response, error in
            if error != nil{
                print(error!.message)
                provisionProfileHandler(error)
                return
            }
            
            /*
            If you are using Xcode Simulator, the "NetworkUtils.installProfile" function will not work.
            The "NetworkUtils.installProfile" function must be commented out / removed
            restoring only the code commented below as an example.
             */
//            provisionProfileHandler(error)
            
            NetworkUtils.installProfile(data: data, handler: { error in
                provisionProfileHandler(error)
            })
        })
    }
    
    private func validateIfIsInitializedAnd(signingStateAtLeast: SigningState) -> OpenRoamingError? {
        if !isSdkRegistered() {
            print(TAG, OpenRoamingError.TokenEmpty.message)
            return OpenRoamingError.TokenEmpty
        }
        
        if (sharedPref.appId == nil) {
            print(TAG, OpenRoamingError.NotInitialized.message)
            return OpenRoamingError.NotInitialized
        }

        if (signingStateAtLeast.hashValue < sharedPref.signingState.hashValue) {
            print(TAG, OpenRoamingError.NotSigned.message)
            return OpenRoamingError.NotSigned
        }
        return nil
    }
    
    private func getUserDetails(userDetailsHandler: @escaping UserDetailsHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.signed) {
            userDetailsHandler(nil, error)
            return
        }
        
        GetUserDetailsService().start(serviceHandler: { data, response, error in
            if error != nil {
                userDetailsHandler(nil, error)
                return
            }
            if data == nil {
                userDetailsHandler(nil, .ServiceBadResponse)
                return
            }
            
            do {
                let dataReturned = try UserDetail(data: data!)
                userDetailsHandler(dataReturned, nil)
            } catch {
                userDetailsHandler(nil, .ServiceBadResponse)
            }
        })

    }
    
    private func getUser(userAccountDetailsHandler: @escaping UserAccountDetailsHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.signed) {
            userAccountDetailsHandler(nil, error)
            return
        }
        
        UserService().start(serviceHandler: { data, response, error in
            if error != nil {
                userAccountDetailsHandler(nil, error)
                return
            }
            guard let data = data else {
                userAccountDetailsHandler(nil, .ServiceBadResponse)
                return
            }
        
            do {
                let dataReturned = try UserDetail(data: data)
                userAccountDetailsHandler(dataReturned, nil)
            }
            catch {
                print(OpenRoamingError.ServiceBadResponse)
                userAccountDetailsHandler(nil, OpenRoamingError.ServiceBadResponse)
            }
        })
    }
    
    
    private func getUsageStatistics(usageStatisticsHandler: @escaping UsageStatisticsHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.signed) {
            usageStatisticsHandler(nil, error)
            return
        }
        
        let service = UsageStatisticsService()
        service.start(serviceHandler: { data, response, error in
            if error != nil {
                usageStatisticsHandler(nil, error)
                return
            }
            guard let data = data else {
                usageStatisticsHandler(nil, .ServiceBadResponse)
                return
            }

            let usageData = UsageStatistics(width: data)
            usageStatisticsHandler(usageData, nil)

        })
    }
    
    private func profileExistence(profileExistenceHandler: @escaping ProfileExistenceHandler) {
        let existence = self.sharedPref.domainName != nil
        profileExistenceHandler(existence, nil)
    }
    
    private func setPrivacySettings(acceptPrivacySettings: Bool, privacySettingsHandler: @escaping PrivacySettingsHandler) {
        
        let service = SetPrivacySettingsService()
        service.start(acceptPrivacySettings: acceptPrivacySettings, serviceHandler: { data, response, error in
            if error != nil{
                print(error!.message)
            }
            DispatchQueue.main.async {
                privacySettingsHandler(acceptPrivacySettings, error)
            }
        })
    }
    
    private func getPrivacySettings(getPrivacySettingsHandler: @escaping GetPrivacySettingsHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.signed) {
           getPrivacySettingsHandler(nil, error)
           return
        }
        
        let service = GetPrivacySettingsService()
        service.start(serviceHandler: { data, response, error in
            guard let data = data else {
                return
            }
            
            let str = String(decoding: data, as: UTF8.self)
            let privacy = NSString(string: str.lowercased()).boolValue
            getPrivacySettingsHandler(privacy, nil)
        })
    }
    
    private func associatePushIdentifier(pushIdentifier: String, associatePushHandler: @escaping AssociatePushHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.signed) {
           associatePushHandler(error)
           return
        }
        
        let service = AssociatePushService()
        service.start(pushIdentifier: pushIdentifier, optIn: true, serviceHandler: { data, response, error in
            if error != nil{
                print(error!.message)
            }
            DispatchQueue.main.async {
                self.sharedPref.pushIdentifier = pushIdentifier
                associatePushHandler(error)
            }
        })
    }
    
    private func dissociatePushIdentifier(associatePushHandler: @escaping AssociatePushHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.signed) {
           associatePushHandler(error)
           return
        }
        
        let service = AssociatePushService()
        service.start(pushIdentifier: "", optIn: false, serviceHandler: { data, response, error in
            if error != nil{
                print(error!.message)
            }
            DispatchQueue.main.async {
                self.sharedPref.pushIdentifier = ""
                associatePushHandler(error)
            }
        })
    }
    
    private func isAssociatedPush() -> Bool {
        return self.sharedPref.pushIdentifier.isEmpty ? false : true
    }
    
    private func associateUser(serverAuthCode: String, serviceName: ServiceName, signingHandler: @escaping SigningHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.unsigned) {
            signingHandler(error)
            return
        }
        ServerAuthCodeService().start(serverAuthCode: serverAuthCode, serviceName: serviceName, serviceHandler: { data, response, error in
            if error == nil {
                self.sharedPref.signingState = SigningState.signed
                signingHandler(nil)
            }
            else{
                signingHandler(error)
            }
        })
    }
    
    private func associateUser(idType: IdType, id: String, signingHandler: @escaping SigningHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.unsigned) {
            signingHandler(error)
            return
        }
        
        if idType == .email {
            if !StringUtils.validateEmail(id) {
                signingHandler(.InvalidEmail)
                return
            }
        }
        
        AssociateUserService().start(idType: idType, id: id, serviceHandler: { data, response, error in
            if error == nil {
                self.sharedPref.signingState = SigningState.signed
                signingHandler(nil)
            }
            else{
                signingHandler(error)
            }
        })
    }
    
    private func deleteUser(deleteUserHandler: @escaping DeleteUserHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.signed) {
            deleteUserHandler(error)
            return
        }
        
        let service = DeleteAccountService()
        service.start(serviceHandler: { data, response, error in
            if error != nil {
                deleteUserHandler(error)
                return
            }
            NetworkUtils.deleteProfile()
            self.sharedPref.signingState = SigningState.unsigned
            self.sharedPref.sdkToken = ""
            deleteUserHandler(nil)
        })
    }
    
    private func deleteProfile(deleteProfileHandler: @escaping DeleteProfileHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.signed) {
            deleteProfileHandler(error)
            return
        }
        
        let service = DeleteProfileService()
        service.start(serviceHandler: { data, response, error in
           if error != nil {
               deleteProfileHandler(error)
               return
           }
           NetworkUtils.deleteProfile()
           self.sharedPref.signingState = SigningState.unsigned
           self.sharedPref.sdkToken = ""
           deleteProfileHandler(nil)
        })
    }
    
    private func updateUserDetails(userDetail: UserDetail, updateUserHandler: @escaping UpdateUserHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.signed) {
            updateUserHandler(error)
            return
        }
        
        getUser(userAccountDetailsHandler: {
            user, error in
            
            if error != nil  || user == nil {
                updateUserHandler(OpenRoamingError.InvalidEmail)
                return
            }
            
            UpdateUserService().start(userDetail: userDetail, serviceHandler: { data, response, error in
                
                guard let userEmail = user?.email else {
                    print(OpenRoamingError.InvalidEmail)
                    updateUserHandler(OpenRoamingError.InvalidEmail)
                    return
                }
                
                if error != nil {
                    print(OpenRoamingError.ServiceBadResponse)
                    updateUserHandler(OpenRoamingError.ServiceBadResponse)
                } else if userEmail.isEmpty {
                    print(OpenRoamingError.InvalidEmail)
                    updateUserHandler(OpenRoamingError.InvalidEmail)
                } else {
                    updateUserHandler(nil)
                }
            })
        })
    }

    private func handleNotification(message: String, notificationHandler: @escaping NotificationHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.unsigned) {
            notificationHandler(nil, nil, error)
        }
        
        if(!self.isAssociatedPush()) {
            print(OpenRoamingError.PushNotificationNotAssociatedException)
        }
        
        let openRoamingInterest: [String] = ["<OR - install profile>", "<OR - delete profile>"]
        
        switch message {
        case let message where message.contains(openRoamingInterest[0]):
            let dataString = message.replacingOccurrences(of: openRoamingInterest[0] + "\n", with: "")
            let data = Data(dataString.utf8)
            NetworkUtils.installProfile(data: data, handler: { error in
                DispatchQueue.main.async {
                    if error != nil {
                        notificationHandler(nil, nil, error)
                    } else {
                        notificationHandler(true, nil, nil)
                    }
                }
            })
        case let message where message.contains(openRoamingInterest[1]):
            NetworkUtils.deleteProfile()
            notificationHandler(true, nil, nil)
        default:
            notificationHandler(false, message, nil)
        }
    }
    
    private func getLocation(locationHandler: @escaping LocationHandler) {
        if let error = validateIfIsInitializedAnd(signingStateAtLeast: SigningState.signed) {
            locationHandler([(nil, nil)], error)
            return
        }
        GetLocationService().start(serviceHandler: { data, response, error in
            if error == nil && data != nil {
                do {
                    let jsonResult: NSDictionary = try (JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary)!
                    let x = Double(jsonResult["x"] as! String)
                    let y = Double(jsonResult["y"] as! String)
                    locationHandler([(x, y)], nil)
                } catch {
                    locationHandler([(nil, nil)], OpenRoamingError.ServiceBadResponse)
                }
            } else {
                locationHandler([(nil, nil)], error)
            }
        })
    }
        
    //MARK: Public section
    
    /**
    * Initial method that must be called in order to perform the operation of other methods.
    * Responsible for initializing with Open Roaming.
    *
    * Parameter appId: Bundle ID or package name
    * Parameter dnaSpacesKey: API key from DNAspaces
    * Parameter registerSdkHandler: Lambda expression to be executed at the end of the method
    * that provides the current state of application (SIGNED or UNSIGNED)
    */
    static public func registerSdk(appId: String, dnaSpacesKey: String, registerSdkHandler: @escaping RegisterSdkHandler) {
        OpenRoaming.obj.registerSdk(appId: appId, dnaSpacesKey: dnaSpacesKey, registerSdkHandler: registerSdkHandler)
    }
    
    /**
    * Returns if the SDK is registered.
    */
    static public func isSdkRegistered() -> Bool {
        return OpenRoaming.obj.isSdkRegistered()
    }
    
    /**
    * Method responsible for enabling push notification between Open Roaming and the Application
    *
    * Parameter pushIdentifier: Push Notification Identifier Token
    * Parameter associatePushHandler: Lambda expression to be executed at the end of the method
    * that provides an error message if the initialization was not successful
    */
    static public func associatePushIdentifier(pushIdentifier: String, associatePushHandler: @escaping AssociatePushHandler) {
        OpenRoaming.obj.associatePushIdentifier(pushIdentifier: pushIdentifier, associatePushHandler: associatePushHandler)
    }
    
    /**
    * Method responsible for disable push notification between Open Roaming and the Application
    *
    * Parameter associatePushHandler: Lambda expression to be executed at the end of the method
    * that provides an error message if the initialization was not successful
    */
    static public func dissociatePushIdentifier(associatePushHandler: @escaping AssociatePushHandler) {
        OpenRoaming.obj.dissociatePushIdentifier(associatePushHandler: associatePushHandler)
    }
    
    static public func isPushAssociated() -> Bool {
        OpenRoaming.obj.isAssociatedPush()
    }
    
    /**
    * Handle push notification and perform OpenRoaming functions if necessary.
    * Whenever a push notification is received, the application must call this method in order to
    * check if this is an Open Roaming system message or not.
    *
    * If this is an OR system message, the method will handle it and return an indication that the
    * application should disregard the message:
    * Parameter disregardNotification: Passed to notificationHandler equal to False.
    *
    * If this is not an OR system message, the method will ignore it and return an indication that
    * the application must handle the message itself:
    * Parameter disregardNotification: Passed to notificationHandler equal to False.
    * In this case, notificationHandler will also receive as a parameter the message to be handled by application.
    *
    * Parameter message: Message from push notification
    * Parameter notificationHandler: As a result of the method, the following information is provided as parameter to
    * be used by lambdas expressions notificationHandler:
    * ->  Parameter disregardNotification: Provides if the message is of interest to OpenRoaming (True/False)
    * ->  Parameter message (optional):  Provides the message notification push. Message will only have value
    * if disregardNotification has the value “False”.
    */
    static public func handleNotification(message: String, notificationHandler: @escaping NotificationHandler) {
        OpenRoaming.obj.handleNotification(message: message, notificationHandler: notificationHandler)
    }
    
    /**
    * Method called to authenticate the user and, if the authentication is successful, it associates the user to the device at Open Roaming backend service.
    * User must be authenticated before being associated to the device. The SDK will use and provide the authentication URL to be
    * rendered by the application. If the authentication is successful, the user is associated to the device at OpenRoaming.
    *
    * Parameter signingView: Webview component that will redirect to an authentication page based on the received serviceName
    * (e.g: if the service name is “Google”, then this component will redirect to Google Authentication page).
    * Parameter serviceName: Which OpenRoaming identity providers should be used to identify the user.
    * (Supported values are: oneid_oauth2, google_oauth2 and apple_oauth2).
    * Parameter signingHandler: Lambda expression to be executed at the end of the method
    * that provides an error message if the initialization was not successful.
    *
    * See: SigningView
    */
    static public func associateUser(signingView: SigningView, serviceName: String, signingHandler: @escaping SigningHandler) {
        OpenRoaming.obj.associateUser(signingView: signingView, serviceName: serviceName, signingHandler: signingHandler)
    }
    
    /**
    * Method called to associate the user to the device at Open Roaming backend service.
    * The user is supposed to have been authenticated previously and they will be identified at the
    * Open Roaming backend service via oAuth token that is passed as parameter to this method.
    *
    * Parameter serverAuthCode: Authentication Token
    * Parameter serviceName Which OpenRoaming identity providers should be used to identify the user.
    * (Supported values are: oneid_oauth2, google_oauth2 and apple_oauth2).
    * Parameter signingHandler: Lambda expression to be executed at the end of the method
    * that provides an error message if the initialization was not successful.
    */
     static public func associateUser(serverAuthCode: String, serviceName: ServiceName, signingHandler: @escaping SigningHandler) {
         OpenRoaming.obj.associateUser(serverAuthCode: serverAuthCode, serviceName: serviceName, signingHandler: signingHandler)
     }
     
    /**
    * Method called to associate the user to the device at Open Roaming backend service.
    * No authentication is required in this case. User will be identified solely via an id passed as parameter to this method.
    *
    * Parameter idType: Type of information used to identify the user.
    * Allowed values are: E-mail, Phone number, Opaque Id and Token.
    * Parameter id: Value of the user identification
    * Parameter signingHandler: Lambda expression to be executed at the end of the method
    * that provides an error message if the initialization was not successful.
    */
    static public func associateUser(idType: IdType, id: String, signingHandler: @escaping SigningHandler) {
         OpenRoaming.obj.associateUser(idType: idType, id: id, signingHandler: signingHandler)
     }
    
    /**
    * Method for updating user information, such as email, phone number, username, age and zip code
    *
    * Parameter userDetail: Data model containing user details
    * Parameter updateUserHandler: Lambda expression to be executed at the end of the method
    * that provides an error message if the initialization was not successful
    */
    static public func updateUserDetails(userDetail: UserDetail, updateUserHandler: @escaping UpdateUserHandler) {
        OpenRoaming.obj.updateUserDetails(userDetail: userDetail, updateUserHandler: updateUserHandler)
    }
    
    /**
    * Get user informations
    *
    * Parameter userDetailsHandler: Lambda expression to be executed at the end of the method
    * that receives an UserDetail object containing user details if the call is successfull and, if not, the handler receives a null UserDetail object.
    * Also, if the call is successful, the handler receives a null error. If not, it provides an error message.
    */
    static public func getUserDetails(userDetailsHandler: @escaping UserDetailsHandler) {
        OpenRoaming.obj.getUserDetails(userDetailsHandler: userDetailsHandler)
        
        //MOCK
        //userDetailsHandler(UserDetail(), nil)
    }
    
    /**
    * Delete the user and all files related to them on the device
    *
    * Parameter deleteUserHandler: Lambda expression to be executed at the end of the method
    * that provides an error message if the initialization was not successful
    */
    static public func deleteUser(deleteUserHandler: @escaping DeleteUserHandler) {
        OpenRoaming.obj.deleteUser(deleteUserHandler: deleteUserHandler)
    }
    
    /**
    * Delete the profile and all files related to them on the device
    *
    * Parameter deleteProfileHandler: Lambda expression to be executed at the end of the method
    * that provides an error message if the initialization was not successful
    */
    static public func deleteProfile(deleteProfileHandler: @escaping DeleteProfileHandler) {
        OpenRoaming.obj.deleteProfile(deleteProfileHandler: deleteProfileHandler)
    }
    
    /**
    * Gets information about Privacy Settings
    *
    * Parameter getPrivacySettingsHandler: Lambda expression to be executed at the end of the method
    * that Provides if the user accepted (true) or declined (false) the privacy settings and
    * provides an error message if the initialization was not successful.
    */
    static public func getPrivacySettings(getPrivacySettingsHandler: @escaping GetPrivacySettingsHandler) {
        OpenRoaming.obj.getPrivacySettings(getPrivacySettingsHandler: getPrivacySettingsHandler)
    }
    
    /**
    * Sets information about Privacy Settings
    *
    * Parameter acceptPrivacySettings: Contains the boolean value according to user privacy settings option on Account screen
    * Parameter privacySettingsHandler: Lambda expression to be executed at the end of the method
    * that Provides the information if the user accepted (true) or declined (false) the privacy settings and
    * provides  an error message if the initialization was not successful.
    */
    static public func setPrivacySettings(acceptPrivacySettings: Bool, privacySettingsHandler: @escaping PrivacySettingsHandler) {
        OpenRoaming.obj.setPrivacySettings(acceptPrivacySettings: acceptPrivacySettings, privacySettingsHandler: privacySettingsHandler)
    }
    
    /**
    * Method for obtaining user usage statistics data, such as connection date, connection time, device used and device ssid
    *
    * Parameter usageStatisticsHandler: Lambda expression to be executed at the end of the method
    * that provides the List of Usage Statistics data model
    */
    static public func getUsageStatistics(usageStatisticsHandler: @escaping UsageStatisticsHandler) {
        OpenRoaming.obj.getUsageStatistics(usageStatisticsHandler: usageStatisticsHandler)
    }
    
    /**
    * Method that obtains user location data.
    *
    * Parameter locationHandler: Lambda expression to be executed at the end of the method
    * that provides the coordinates X and Y or provides an error message if the initialization was not successful.
    */
    static public func getLocation(locationHandler: @escaping LocationHandler) {
        OpenRoaming.obj.getLocation(locationHandler: locationHandler)
    }
        
    /**
    * Download and install a profile on device
    * Parameter provisionProfileHandler: Lambda expression to be executed at the end of the method
    * that provides an error message if the initialization was not successful.
    */
    static public func installProfile(provisionProfileHandler: @escaping ProvisionProfileHandler) {
        OpenRoaming.obj.installProfile(provisionProfileHandler: provisionProfileHandler)
    }
    
    /**
    * Checks if there is a profile already provisioned on the device
    *
    * Parameter profileExistenceHandler: Lambda expression to be executed at the end of the method
    * that informs if a profile already exists on the device or not and provides an error message if the initialization was not successful.
    */
    static public func profileExistence(profileExistenceHandler: @escaping ProfileExistenceHandler) {
        OpenRoaming.obj.profileExistence(profileExistenceHandler: profileExistenceHandler)
    }
}
