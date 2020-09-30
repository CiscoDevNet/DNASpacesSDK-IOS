//
//  GetLocationService.swift
//  OpenRoaming
//
//  Created by Fabiana Garcia on 7/7/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class GetLocationService: Service {
    
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let urlString = Urls.getLocationUrl()
        let requestData = RequestData(url: urlString, headerParams: headerParams, method: HTTPMethod.GET)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
