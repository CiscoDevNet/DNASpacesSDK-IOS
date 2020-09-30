//
//  RegisterService.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 05/06/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class RegisterService: Service {
    
    private var registerSdkHandler: RegisterSdkHandler!
    
    /**
    * Not implemented.
    * Use start(context: Context, acceptPrivacySettings: Boolean, serviceHandler: ServiceHandler).
    */
    override func start(serviceHandler: @escaping ServiceHandler) {
        fatalError("Not implemented")
    }
    
    func start(handler: @escaping RegisterSdkHandler) {
        self.registerSdkHandler = handler
        let sharedPrefs = SharedPrefs()
        if sharedPrefs.clientPublicKey == nil {
            generateKeys(sharedPrefs: sharedPrefs)
            
            if sharedPrefs.clientPublicKey == nil {
                registerSdkHandler(SigningState.unsigned, OpenRoamingError.TokenEmpty)
                return
            }
        }
        
        if let encryptedPublicKey = CryptoUtilRSA.encrypt(publicKey: sharedPrefs.clientPublicKey!, usingPublicKey: Constants.serverPublicKey) {
       
            var headerParams = Array<HeaderParam>()
            headerParams.append(HeaderParam(name: "Content-Type", value: "application/json"))
            headerParams.append(HeaderParam(name: "x-dnaspaces-apikey", value: dnaSpacesKey()))
            headerParams.append(HeaderParam(name: "Authorization", value: encryptedPublicKey))

            onStart(headerParams: headerParams, serviceHandler: {_,_,_ in })
        }
        else {
           registerSdkHandler(SigningState.unsigned, OpenRoamingError.TokenEmpty)
           return
       }
    }

    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let url = Urls.registerUrl()
        let jsonValues:NSMutableDictionary = NSMutableDictionary()
        jsonValues.setValue(guid(), forKey: "guid")
        jsonValues.setValue(appId(), forKey: "appId")
        jsonValues.setValue("apple", forKey: "platform")

        let jsonData = try! JSONSerialization.data(withJSONObject: jsonValues)
        let body = NSString(data: jsonData, encoding: String.Encoding.utf8.rawValue)! as String

        let requestData = RequestData(url: url, headerParams: headerParams, method: HTTPMethod.POST, body: body)
        call(requestData: requestData, serviceHandler: { data, response, error in
            self.readSdkToken(data, response, error, self.registerSdkHandler)
        })
    }
    
    internal func readSdkToken(_ data: Data?, _ response: URLResponse?, _ error: OpenRoamingError?, _ registerSdkHandler: RegisterSdkHandler) {
        do {
            let sharedPref = SharedPrefs()
            let jsonResult: NSDictionary = try (JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary)!
            
            let encriptedSdkToken = jsonResult["sdkToken"] as! String
            let encriptedKey = jsonResult["key"] as! String
            let clientPrivateKey = sharedPref.clientPrivateKey!
            
            let decryptedKeyData = CryptoUtilRSA.decrypt(base64EncodedMessage: encriptedKey, privateKey: clientPrivateKey)!
            let decryptedKey = String(data: decryptedKeyData, encoding: .utf8)
            
            if decryptedKey == nil {
                registerSdkHandler(SigningState.unsigned, OpenRoamingError.RegisterNotAuthorized)
                return
            }

            do {
                let decryptedSdkToken = try CryptoUtilsAES(decryptedKey!).decrypt(string: encriptedSdkToken)
                sharedPref.sdkToken = decryptedSdkToken
                registerSdkHandler(sharedPref.signingState, nil)
            }
            catch {
                registerSdkHandler(SigningState.unsigned, OpenRoamingError.RegisterNotAuthorized)
            }
        }
        catch {
            registerSdkHandler(SigningState.unsigned, OpenRoamingError.RegisterNotAuthorized)
        }
    }

    private func generateKeys(sharedPrefs: SharedPrefs){
        if let keyPair = CryptoUtilRSA.generateKeyPair() {
            sharedPrefs.clientPublicKey = CryptoUtilRSA.keyToString(keyPair.publicKey)
            sharedPrefs.clientPrivateKey = CryptoUtilRSA.keyToString(keyPair.privateKey)
        }
    }
}
