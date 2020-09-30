//
//  Urls.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 23/04/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

struct Urls {
    
    static let domain = "www.cisco.com"
    static let redirectURL = "https://dummy.com/devicetoken.html"
    static let baseURL = "https://dev-api.openroaming.net/"
    static let profileUrl = "api/profiles/v1/user/passpoint/"
    static let authURL = "api/profiles/v1/oauth2?scope=create_profile&state=sample_app_data&response_type=token"
    static let acceptTermsURL = "api/profiles/v1/user/accepttos/true?access_token="
    static let usageStatisticsURL = "api/profiles/v1/user/stats?access_token="
    static let userURL = "api/profiles/v1/user?access_token="
    static let shareEmailURL = "api/profiles/v1/user/anonymousemail/"
    static let openRoamingURL = "https://www.cisco.com/c/en_in/solutions/enterprise-networks/802-11ax-solution/openroaming.html"
    static let privacyPolicyURL = "https://www.cisco.com/c/en/us/about/legal/privacy-full.html"
    static let termsURL = "https://openroaming.org/terms-conditions/"
    static let accountDetailsURL = "api/profiles/v1/user/identity/"
    static let accountDeleteURL = "api/profiles/v1/user"
    static let accountlogoutURL = "api/profiles/logout"
    static let deleteDeviceURL = "api/profiles/v1/user/passpoint/"
    static let usageURL = "api/profiles/v1/user/stats"
    static let logoutUrlAPI = "api/profiles/logout?access_token="
    static let deleteAccountURL = "api/profiles/v1/user?access_token="
    static let passPointUrlAPI = "api/profiles/v1/user/passpoint/"


    static func serviceSigningUrl(loginWith: String, loginType: String, appId : String) -> String {
        let url =  baseURL + authURL +
            "&redirect_uri=" + redirectURL.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)!
        return url + "&client_id="+appId+"&login_with=" + loginWith +
            "&loginType=" + loginType
    }
    
    static func profileUrl(accessToken: String, venderId: String, deviceName: String, deviceType: String, appId : String) -> String {
        return  baseURL + profileUrl + venderId + "/iosProfile.xml?type=apple" + "&name=" + deviceName + "&devicetype=" + deviceType + "&client_id=" + appId + "&access_token=" + accessToken
    }
    
    static func usageStatisticsUrl(accessToken: String) -> String {
        return baseURL + usageStatisticsURL + accessToken
    }
    
    static func userUrl(accessToken: String) -> String {
        return baseURL + userURL + accessToken
    }
    
    static func acceptTermsUrl(accessToken: String) -> String {
        return baseURL + acceptTermsURL + accessToken
    }

    static func setPrivacySettingsUrl(accessToken: String, acceptPrivacySettings: Bool) -> String {
        return baseURL + shareEmailURL + (acceptPrivacySettings ? "true" : "false") + "?access_token=" + accessToken
    }
    
    static func getUserDetailsUrl(accessToken: String, identity: String) -> String {
        return baseURL + accountDetailsURL + identity + "?access_token=" + accessToken
    }
    
    static func getPrivacySettingsUrl(accessToken: String) -> String {
        return baseURL + shareEmailURL + "?access_token=" + accessToken
    }
    
    static func logoutUrl(accessToken: String) -> String {
        return baseURL + logoutUrlAPI + accessToken
    }
    
    static func deleteAccountUrl(accessToken: String) -> String {
        return baseURL + deleteAccountURL + accessToken
    }
    
    static func deviceProfileUrl(deviceId: String, accessToken: String) -> String {
        return baseURL + passPointUrlAPI + deviceId + "?access_token=" + accessToken
    }
    
    
    //Version 2.0 URLS
    static let apiUrl = "https://dev-api.openroaming.net"
    static func registerUrl() -> String {
        apiUrl + "/sdk/v1/register"
    }
    static func refreshTokenUrl() -> String {
        apiUrl + "/sdk/v1/refreshsdktoken"
    }
    
    static func oauthTokenUrl() -> String {
        apiUrl + "/sdk/v1/authenticate/authtoken"
    }
    
    static func associateUserUrl() -> String {
        apiUrl + "/sdk/v1/associateuser" //todo: check real url
    }
    
    static func associatePushUrl() -> String {
        apiUrl + "/sdk/v1/associatepush"
    }
    
    static func userDetailsUrl() -> String {
        apiUrl + "/sdk/v1/user"
    }
    
    static func userDeleteAccountUrl() -> String {
        apiUrl + "/sdk/v1/user"
    }
    
    static func userDeleteProfileAccountUrl() -> String {
        apiUrl + "/sdk/v1/profile"
    }

    static func updateUserUrl() -> String {
        apiUrl + "/sdk/v1/user"
    }
    
    static func getLocationUrl() -> String {
        apiUrl + "/sdk/v1/getlocation"
    }
    
    static func authenticationUrl(serviceName: String) -> String {
        apiUrl + "/sdk/v1/authenticate/authtoken?authType=" + serviceName
    }
    
    static func installProfileUrl() -> String {
        apiUrl + "/sdk/v1/profile"
    }
    
    static func privacySettingsUrl() -> String {
        apiUrl + "/sdk/v1/user/anonymousemail"
    }
    
    static func usageStatisticsUrl() -> String {
        apiUrl + "/sdk/v1/user/stats"
    }
}
