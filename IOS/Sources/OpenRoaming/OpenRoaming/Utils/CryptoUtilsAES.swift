//
//  CryptoUtilsAES.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 17/06/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

public class CryptoUtilsAES {
    var aes: AES!
    
    init (_ key: String) throws {
        self.aes = try AES(keyString: key)
    }
    
    func encrypt(string plain: String) throws -> String? {
        do {
            let cipher = try aes.encrypt(plain)
            return String(data: cipher, encoding: .utf8)
        }
    }
    
    func decrypt(string cipher: String) -> String? {
        do {
            let data = Data(base64Encoded: cipher)!
            let value = try aes.decrypt(data)
            return value
        }
        catch {
            return nil
        }
    }
}
