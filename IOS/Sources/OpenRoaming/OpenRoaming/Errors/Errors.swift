//
//  Errors.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 16/04/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

public enum OpenRoamingError : Error {
    case NotInitialized
    case TermsNotAccepted
    case ServiceBadResponse
    case RegisterNotAuthorized
    case Hotspot2NotSupported
    case TokenEmpty
    case LoginFailed
    case NotSigned
    case InvalidEmail
    case InvalidPhoneNumber
    case DeviceCurrentDeleteSelfException
    case PushNotificationNotAssociatedException
    
    public var message: String {
        get {
            switch self {
                case .NotInitialized:
                    return "Call OpenRoaming.initialize() to initialize the SDK before continue."
                case .TermsNotAccepted:
                    return "Call OpenRoaming.acceptTerms() to accept OpenRoaming TOS & Privacy Policy before continue."
                case .ServiceBadResponse:
                    return "Bad response from service! Please check your internet connectivity."
                case .RegisterNotAuthorized:
                    return "SDK registration was not authorized."
                case .Hotspot2NotSupported:
                    return "Device doest not support Hotspot 2.0."
                case .TokenEmpty:
                    return "Fail on get the access token."
                case .LoginFailed:
                    return "Login has failed."
                case .NotSigned:
                    return "Not signed in."
                case .InvalidEmail:
                    return "Invalid e-mail."
                case .InvalidPhoneNumber:
                    return "Invalid phone number."
                case .DeviceCurrentDeleteSelfException:
                    return "This device is the same as the one being used, it is not possible to perform the deletion."
                case .PushNotificationNotAssociatedException:
                    return "Associate Push Notification needs to be activated for the method to be used."
            }
        }
    }
}
