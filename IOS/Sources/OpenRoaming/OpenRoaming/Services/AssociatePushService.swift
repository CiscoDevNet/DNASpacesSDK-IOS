//
//  AssociatePushService.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 24/06/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation


class AssociatePushService: Service {
    
    var pushIdentifier:String?
    var optIn:Bool=true
    
    override func start(serviceHandler: @escaping ServiceHandler) {
        fatalError("Not implemented")
    }
    
    func start(pushIdentifier: String?, optIn: Bool, serviceHandler: @escaping ServiceHandler) {
        self.pushIdentifier = pushIdentifier
        self.optIn = optIn
        super.start(serviceHandler: serviceHandler)
    }
    
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let url = Urls.associatePushUrl()
        var body: String? = nil
        if self.pushIdentifier != nil {
            let jsonValues:NSMutableDictionary = NSMutableDictionary()
            jsonValues.setValue(self.pushIdentifier!, forKey: "pushIdentifier")
            jsonValues.setValue(self.optIn, forKey: "optIn")

            let jsonData = try! JSONSerialization.data(withJSONObject: jsonValues)
            body = NSString(data: jsonData, encoding: String.Encoding.utf8.rawValue)! as String
        }

        let requestData = RequestData(url: url, headerParams: headerParams, method: HTTPMethod.POST, body: body)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
