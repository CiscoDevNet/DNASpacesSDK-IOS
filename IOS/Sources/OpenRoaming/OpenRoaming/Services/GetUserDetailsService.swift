//
//  GetUserDetailsService.swift
//  OpenRoaming
//
//  Created by Willian Barbosa on 11/05/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import UIKit

class GetUserDetailsService: Service {
    
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let urlString = Urls.userDetailsUrl()
        let requestData = RequestData(url: urlString, headerParams: headerParams, method: HTTPMethod.GET)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
