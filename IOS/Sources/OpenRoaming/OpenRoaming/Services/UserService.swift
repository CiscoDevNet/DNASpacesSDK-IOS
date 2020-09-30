//
//  UserService.swift
//  OpenRoaming
//
//  Created by olivier duque campos on 17/06/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class UserService: Service {
    
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let urlString = Urls.userDetailsUrl()
        let requestData = RequestData(url: urlString, headerParams: headerParams, method: HTTPMethod.GET)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
