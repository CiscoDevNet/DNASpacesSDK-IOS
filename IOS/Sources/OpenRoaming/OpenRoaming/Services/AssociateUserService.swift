//
//  AssociateUserService.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 16/06/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class AssociateUserService: Service {
    var idType: IdType!
    var id: String!
    
    override func start(serviceHandler: @escaping ServiceHandler) {
        fatalError("Not implemented")
    }
    
    func start(idType: IdType, id: String, serviceHandler: @escaping ServiceHandler) {
        self.idType = idType
        self.id = id
        super.start(serviceHandler: serviceHandler)
    }
    
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let url = Urls.associateUserUrl()
        
        let jsonValues:NSMutableDictionary = NSMutableDictionary()
        jsonValues.setValue(self.idType.rawValue.replacingOccurrences(of: "_", with: "-"), forKey: "idType")
        jsonValues.setValue(self.id, forKey: "id")
        
        let jsonData = try! JSONSerialization.data(withJSONObject: jsonValues)
        let body = NSString(data: jsonData, encoding: String.Encoding.utf8.rawValue)! as String
 
        let requestData = RequestData(url: url, headerParams: headerParams, method: HTTPMethod.POST, body: body)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
