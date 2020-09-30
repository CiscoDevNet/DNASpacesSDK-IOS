//
//  CryptoUtilRSA.swift
//  OpenRoaming
//
//  Created by olivier duque campos on 22/06/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

public struct KeyPair {
    var privateKey: SecKey
    var publicKey: SecKey
}

public class CryptoUtilRSA {

    static public func generateKeyPair(size: Int = 2048) -> KeyPair? {
           var publicKeySec, privateKeySec: SecKey?
        
            let publicKeyAttr: [NSObject: NSObject] = [
                        kSecAttrIsPermanent:true as NSObject,
                        kSecClass: kSecClassKey, // added this value
                        kSecReturnData: kCFBooleanTrue] // added this value
            let privateKeyAttr: [NSObject: NSObject] = [
                        kSecAttrIsPermanent:true as NSObject,
                        kSecClass: kSecClassKey, // added this value
                        kSecReturnData: kCFBooleanTrue] // added this value
        
            let keyattribute = [
                kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
                kSecAttrKeySizeInBits as String : size,
                kSecPublicKeyAttrs as NSObject : publicKeyAttr,
                kSecPrivateKeyAttrs as NSObject : privateKeyAttr
            ] as CFDictionary

            SecKeyGeneratePair(keyattribute, &publicKeySec, &privateKeySec)
    
        if privateKeySec != nil && publicKeySec != nil {
            return KeyPair(privateKey: privateKeySec!, publicKey: publicKeySec!)
        }
        return nil
    }
    
    static public func exportAsData(publicKey: String, size: Int = 2048) -> Data? {
        
        let key = stringToKey(publicKey)
        if let cfdata = SecKeyCopyExternalRepresentation(key!, nil) {
           let pubKeyData:Data = cfdata as Data

            let keyType = kSecAttrKeyTypeRSA
            let keySize = size
            let exportImportManager = CryptoExportImportManager()
            let exportablePEMKey = exportImportManager.exportRSAPublicKeyToDER(pubKeyData, keyType: keyType as String, keySize: keySize)
            return exportablePEMKey
            
        }
        return nil
    }
    
    static public func exportAsString(publicKey: String, size: Int = 2048) -> String? {
        if let data = exportAsData(publicKey: publicKey, size: size) {
            return data.base64EncodedString()
        }
        return nil
    }
    
    static public func keyToString(_ key: SecKey) -> String? {
        var strKey = ""
        var error: Unmanaged<CFError>?
        if let cfdata = SecKeyCopyExternalRepresentation(key, &error) {
           let data:Data = cfdata as Data
            let b64Key = data.base64EncodedString()
            strKey = b64Key
        }
        return strKey
    }
    
    static public func stringToKey(_ b64Key: String, size: Int = 2048, keyClass: CFString = kSecAttrKeyClassPublic) -> SecKey? {
        guard let data2 = Data.init(base64Encoded: b64Key) else {
           return nil
        }
    
        let keyDict:[NSObject:NSObject] = [
           kSecAttrKeyType: kSecAttrKeyTypeRSA,
           kSecAttrKeyClass: keyClass,
           kSecAttrKeySizeInBits: NSNumber(value: size),
           kSecReturnPersistentRef: true as NSObject
        ]
    
        guard let key = SecKeyCreateWithData(data2 as CFData, keyDict as  CFDictionary, nil) else {
            return nil
        }
    
        return key
    }
    
    static public func decrypt(message: String, privateKey: String) -> Data? {
        let pKey = stringToKey(privateKey, keyClass: kSecAttrKeyClassPrivate)!
        let cipherText = [UInt8](message.utf8)

        var plainTextSize   = SecKeyGetBlockSize(pKey)
        var plainText = [UInt8](repeating: 0, count: plainTextSize)

        let status = SecKeyDecrypt(pKey, SecPadding.PKCS1, cipherText, plainTextSize, &plainText, &plainTextSize)
        if (status != errSecSuccess) {
            print("Failed Decrypt")
            return nil
        }
        return Data(bytes: plainText, count: plainTextSize)
    }
    
    static public func decrypt(base64EncodedMessage: String, privateKey: String) -> Data? {
        let pKey = stringToKey(privateKey, keyClass: kSecAttrKeyClassPrivate)!
        
        let data : NSData = NSData(base64Encoded: base64EncodedMessage, options: .ignoreUnknownCharacters)!
        let count = data.length / MemoryLayout<UInt8>.size
        var array = [UInt8](repeating: 0, count: count)
        data.getBytes(&array, length:count * MemoryLayout<UInt8>.size)
         
        var plainTextSize = Int(SecKeyGetBlockSize(pKey))
        var plainText = [UInt8](repeating:0, count:Int(plainTextSize))
         
        let status = SecKeyDecrypt(pKey, SecPadding(), array, plainTextSize, &plainText, &plainTextSize)
        if (status != errSecSuccess) {
            print("Failed Decrypt")
            return nil
        }
        return Data(bytes: plainText, count: plainTextSize)
    }
    
    static public func encrypt(message: String, usingPublicKey: String) -> String? {
        guard let key = stringToKey(usingPublicKey)
        else { return nil }
        
        let messageBuffer = [UInt8](message.utf8)

        var keySize   = SecKeyGetBlockSize(key)
        var keyBuffer = [UInt8](repeating: 0, count: keySize)

        let status = SecKeyEncrypt(key, SecPadding.PKCS1, messageBuffer, messageBuffer.count, &keyBuffer, &keySize)
        if (status != errSecSuccess) {
            print("Failed Encrypt")
            return nil
        }
        return Data(bytes: keyBuffer, count: keySize).base64EncodedString()
    }
    
    static public func encrypt(publicKey: String, usingPublicKey: String) -> String? {
        let data: Data = CryptoUtilRSA.exportAsData(publicKey: publicKey)!
        let dataBuffer = [UInt8](data)
        
        let serverKey = stringToKey(usingPublicKey)!
        var cipherBufferSize = SecKeyGetBlockSize(serverKey)
        var cipherBuffer = [UInt8](repeating: 0, count: Int(cipherBufferSize))

        let status =  SecKeyEncrypt(serverKey, SecPadding(), dataBuffer, dataBuffer.count, &cipherBuffer, &cipherBufferSize)
        if (status != errSecSuccess) {
            print("Failed Encrypt")
            return nil
        }
        return Data(bytes: cipherBuffer, count: cipherBufferSize).base64EncodedString()
    }
}
