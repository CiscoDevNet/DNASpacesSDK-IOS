//
//  UsageStatisticsService.swift
//  OpenRoaming
//
//  Created by Fabiana Garcia on 4/27/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

/**
* Gets a device's usage statistics.
*/
class UsageStatisticsService: Service {
    
    /**
     Method responsible to get a device's usage statistics.
     
     Parameter <accessToken>: User's access token.
     Parameter <serviceHandler>: Type of handler to response.
     */
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let urlString = Urls.usageStatisticsUrl()
        let requestData = RequestData(url: urlString, headerParams: headerParams, method: HTTPMethod.GET)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
