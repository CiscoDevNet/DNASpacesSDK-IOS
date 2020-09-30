//
//  JWTDecoder.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 23/07/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class JWTDecoder {
    
    let token: String
    private var json: [String:Any]?
    
    init(_ token: String){
        self.token = token
        self.decode()
    }
    
    private func decode(){
        let arr = token.components(separatedBy:  ".")
        let base64String = arr[1]
        decodeComponent(base64String)
    }
    private func decodeComponent(_ token: String){
        var str = token
        if str.count % 4 != 0 {
            let padlen = 4 - str.count % 4
            str.append(contentsOf: repeatElement("=", count: padlen))
        }

        if let data = Data(base64Encoded: str) {
            do {
                self.json = try JSONSerialization.jsonObject(with: data) as? [String:Any]
            }
            catch {
                print(error)
            }
        }
    }
    
    public func appId() -> String? {
        if json != nil {
            return json!["appId"] as? String
        }
        return nil
    }
    public func tenantId() -> String? {
        if json != nil {
            return json!["tenantId"] as? String
        }
        return nil
    }
    public func guid() -> String? {
        if json != nil {
            return json!["guid"] as? String
        }
        return nil
    }
    public func exp() -> Double? {
        if json != nil {
            return json!["exp"] as? Double
        }
        return nil
    }
    public func expired() -> Bool {
        if let exp = self.exp() {
            let now = Date().timeIntervalSince1970
            if now <= exp {
                return false
            }
        }
        return true
    }
}

