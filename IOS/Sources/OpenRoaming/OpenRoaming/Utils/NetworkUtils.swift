//
//  Network.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 25/04/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation
import NetworkExtension
import SystemConfiguration

public class NetworkUtils {
    
    static public func isConnectedToNetwork() -> Bool {
        var zeroAddress = sockaddr_in(sin_len: 0, sin_family: 0, sin_port: 0, sin_addr: in_addr(s_addr: 0), sin_zero: (0, 0, 0, 0, 0, 0, 0, 0))
        zeroAddress.sin_len = UInt8(MemoryLayout.size(ofValue: zeroAddress))
        zeroAddress.sin_family = sa_family_t(AF_INET)
        let defaultRouteReachability = withUnsafePointer(to: &zeroAddress) {
            $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {zeroSockAddress in
                SCNetworkReachabilityCreateWithAddress(nil, zeroSockAddress)
            }
        }
        var flags: SCNetworkReachabilityFlags = SCNetworkReachabilityFlags(rawValue: 0)
        if SCNetworkReachabilityGetFlags(defaultRouteReachability!, &flags) == false {
            return false
        }
        let isReachable = (flags.rawValue & UInt32(kSCNetworkFlagsReachable)) != 0
        let needsConnection = (flags.rawValue & UInt32(kSCNetworkFlagsConnectionRequired)) != 0
        let ret = (isReachable && !needsConnection)
        
        return ret
    }
    
    static func installProfile(data: Data?, handler: @escaping (OpenRoamingError?) -> Void){
        if (data == nil) {
            handler(OpenRoamingError.LoginFailed)
            return
        }
        else{
            do {
                print(String(data: data!, encoding: .utf8) as Any)
                
                let plist = try PropertyListSerialization.propertyList(from: data!, options: .mutableContainers, format: nil)
                let dict = plist as! [String:AnyObject]
                self.addProfile(dictArray: dict, handler: handler)
            }
            catch let error as NSError {
                print(error.code, error.domain)
                handler(OpenRoamingError.LoginFailed)
                return
            }
        }
    }
    
    static func deleteProfile() {
        let sharedPrefs: SharedPrefs = SharedPrefs()
        let domainName = sharedPrefs.domainName

        NEHotspotConfigurationManager.shared.getConfiguredSSIDs { (ssidsArray) in
            print("ssidsArray.count==(ssidsArray.count)")
            for ssid in ssidsArray {
                print("Connected ssid = ", ssid);
                NEHotspotConfigurationManager.shared.removeConfiguration(forHS20DomainName: domainName ?? "")
            }
        }
        if(domainName != nil) {
            NEHotspotConfigurationManager.shared.removeConfiguration(forHS20DomainName: domainName ?? "")
        }
    }
    
    private static func addProfile(dictArray:[String:AnyObject], handler: @escaping (OpenRoamingError?) -> Void) {
        let content = dictArray["PayloadContent"] as! [[String:AnyObject]]
        var config: [String:AnyObject] = [:]
        for item in content {
            if (item["DomainName"] != nil) {
                config = item
            }
        }
        let domainName = config["DomainName"] as! String;
        let roamingConsortiumOIs = config["RoamingConsortiumOIs"] as! [String]
        // EAP settings
        let eapSettings = config["EAPClientConfiguration"] as! [String:AnyObject]
        let supportedEAPTypes = eapSettings["AcceptEAPTypes"] as! [NSNumber]
        /*  TLS parameters */
        let userName = eapSettings["UserName"] as! String;
        let password = eapSettings["UserPassword"] as! String;
        let trustedServerNames = eapSettings["TLSTrustedServerNames"] as! [String]
        let outerIdentity = eapSettings["OuterIdentity"] as! String;
        let isTLSClientCertificateRequired = true;
        let settings = NEHotspotEAPSettings.init()
        settings.username = userName
        settings.password =  password
        settings.supportedEAPTypes = supportedEAPTypes
        settings.preferredTLSVersion = NEHotspotEAPSettings.TLSVersion._1_0
        settings.isTLSClientCertificateRequired = isTLSClientCertificateRequired
        settings.trustedServerNames = trustedServerNames
        if (eapSettings["TTLSInnerAuthentication"] != nil) {
            settings.ttlsInnerAuthenticationType = self.getTTLSAuthenticationType(authType: eapSettings["TTLSInnerAuthentication"] as! String);
        }
        settings.outerIdentity = outerIdentity
        let hotspotSettings = NEHotspotHS20Settings.init(domainName:domainName, roamingEnabled: true)
        if(eapSettings["NAIRealmNames"] != nil ){
            hotspotSettings.naiRealmNames = eapSettings["NAIRealmNames"] as! [String]
        }
        hotspotSettings.roamingConsortiumOIs = roamingConsortiumOIs
        let hotspotConfig = NEHotspotConfiguration(hs20Settings: hotspotSettings, eapSettings: settings)
        hotspotConfig.joinOnce = false;
        
        NEHotspotConfigurationManager.shared.apply(hotspotConfig) { (error) in
            if error != nil {
                handler(OpenRoamingError.Hotspot2NotSupported)
            }
            else {
                let sharedPref = SharedPrefs()
                sharedPref.domainName = domainName
                handler(nil)
            }
            
        }
    }
    
    private static func getTTLSAuthenticationType (authType: String) -> NEHotspotEAPSettings.TTLSInnerAuthenticationType {
        switch (authType.lowercased()) {
            case "chap":
                return NEHotspotEAPSettings.TTLSInnerAuthenticationType.eapttlsInnerAuthenticationCHAP;
            case "eap":
                return NEHotspotEAPSettings.TTLSInnerAuthenticationType.eapttlsInnerAuthenticationEAP;
            case "mschap":
                return NEHotspotEAPSettings.TTLSInnerAuthenticationType.eapttlsInnerAuthenticationMSCHAP;
            case "mschapv2":
                return NEHotspotEAPSettings.TTLSInnerAuthenticationType.eapttlsInnerAuthenticationMSCHAPv2;
            case "pap":
                return NEHotspotEAPSettings.TTLSInnerAuthenticationType.eapttlsInnerAuthenticationPAP;
            default:
                return NEHotspotEAPSettings.TTLSInnerAuthenticationType.init(rawValue: Int(authType)!)!;
        }
    }
}
