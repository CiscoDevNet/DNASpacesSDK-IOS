//
//  ServerAuthCodeService.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 16/06/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class ServerAuthCodeService: Service {
    var serverAuthCode: String!
    var serviceName: ServiceName!
    
    override func start(serviceHandler: @escaping ServiceHandler) {
        fatalError("Not implemented")
    }
    
    func start(serverAuthCode: String, serviceName: ServiceName, serviceHandler: @escaping ServiceHandler) {
        self.serverAuthCode = serverAuthCode
        self.serviceName = serviceName
        super.start(serviceHandler: serviceHandler)
    }
    
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let url = Urls.oauthTokenUrl()
        
        let jsonValues:NSMutableDictionary = NSMutableDictionary()
        jsonValues.setValue(self.serviceName.rawValue, forKey: "authType")
        jsonValues.setValue(self.serverAuthCode, forKey: "id")
        
        let jsonData = try! JSONSerialization.data(withJSONObject: jsonValues)
        let body = NSString(data: jsonData, encoding: String.Encoding.utf8.rawValue)! as String
 
        let requestData = RequestData(url: url, headerParams: headerParams, method: HTTPMethod.POST, body: body)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
