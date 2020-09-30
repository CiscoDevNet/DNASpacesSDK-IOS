//
//  SharedPrefs.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 10/04/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import UIKit

class SharedPrefs: NSObject {
    
    private let PREF_APP_ID = "PREF_CLIENT_ID"
    private let PREF_CLIENT_PUBLIC_KEY = "PREF_CLIENT_PUBLIC_KEY"
    private let PREF_CLIENT_PRIVATE_KEY = "PREF_CLIENT_PRIVATE_KEY"
    private let DNA_SPACES_KEY = "DNA_SPACES_KEY"
    private let PREF_ACCESS_TOKEN = "PREF_ACCESS_TOKEN"
    private let PREF_SDK_TOKEN = "PREF_SDK_TOKEN"
    private let PREF_TERMS = "PREF_TERMS"
    private let PREF_SIGNING_STATE = "PREF_SIGNING_STATE"
    private let PREF_SIGNING_SERVICE_NAME = "PREF_SIGNING_SERVICE_NAME"
    private let DOMAIN_NAME = "DomainName"
    private let FQDN = "INSTALLED_FQDN"
    private let PREF_PUSH_ID = "PREF_PUSH_ID"
    
    var domainName: String? {
        get {
            UserDefaults.standard.string(forKey: DOMAIN_NAME)
        }
        set(value){
            UserDefaults.standard.set(value, forKey: DOMAIN_NAME)
        }
    }
    
    var appId: String? {
        get {
            UserDefaults.standard.string(forKey: PREF_APP_ID)
        }
        set(value) {
            UserDefaults.standard.set(value, forKey: PREF_APP_ID)
        }
    }
    
    var clientPublicKey: String? {
        get {
            UserDefaults.standard.string(forKey: PREF_CLIENT_PUBLIC_KEY)
        }
        set(value) {
            UserDefaults.standard.set(value, forKey: PREF_CLIENT_PUBLIC_KEY)
        }
    }
    
    var clientPrivateKey: String? {
        get {
            UserDefaults.standard.string(forKey: PREF_CLIENT_PRIVATE_KEY)
        }
        set(value) {
            UserDefaults.standard.set(value, forKey: PREF_CLIENT_PRIVATE_KEY)
        }
    }
    
    var dnaSpacesKey: String? {
        get {
            UserDefaults.standard.string(forKey: DNA_SPACES_KEY)
        }
        set(value) {
            UserDefaults.standard.set(value, forKey: DNA_SPACES_KEY)
        }
    }
    
    var accessToken: String? {
        get {
            UserDefaults.standard.string(forKey: PREF_ACCESS_TOKEN)
        }
        set(value) {
            UserDefaults.standard.set(value, forKey: PREF_ACCESS_TOKEN)
        }
    }
    
    var sdkToken: String? {
        get {
            UserDefaults.standard.string(forKey: PREF_SDK_TOKEN)
        }
        set(value) {
            UserDefaults.standard.set(value, forKey: PREF_SDK_TOKEN)
        }
    }
    
    var acceptTerms: Bool {
        get {
            UserDefaults.standard.bool(forKey: PREF_TERMS)
        }
        set(value) {
            UserDefaults.standard.set(value, forKey: PREF_TERMS)
        }
    }
    
    var signingState: SigningState {
        get {
            let i = UserDefaults.standard.string(forKey: PREF_SIGNING_STATE)
            switch(i){
                case SigningState.signed.rawValue:
                    return SigningState.signed
                default:
                    return SigningState.unsigned
            }
        }
        set(value) {
            UserDefaults.standard.set(value.rawValue, forKey: PREF_SIGNING_STATE)
        }
    }
    
    var signingServiceName: String {
        get {
            UserDefaults.standard.string(forKey: PREF_SIGNING_SERVICE_NAME) ?? ""
        }
        set(value) {
            UserDefaults.standard.set(value, forKey: PREF_SIGNING_SERVICE_NAME)
        }
    }
    
    var fqdn: String {
        get {
            UserDefaults.standard.string(forKey: FQDN) ?? ""
        }
        set(value) {
            UserDefaults.standard.set(value, forKey: FQDN)
        }
    }
    
    var pushIdentifier: String {
        get {
            UserDefaults.standard.string(forKey: PREF_PUSH_ID) ?? ""
        }
        set(value) {
            UserDefaults.standard.set(value, forKey: PREF_PUSH_ID)
        }
    }
    
    func logout() {
        accessToken = nil
        signingState = SigningState.unsigned
    }
}
