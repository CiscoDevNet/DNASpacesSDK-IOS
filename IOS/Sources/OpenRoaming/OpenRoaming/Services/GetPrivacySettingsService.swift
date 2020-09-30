//
//  GetPrivacySettingsService.swift
//  OpenRoaming
//
//  Created by Fabiana Garcia on 5/18/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class GetPrivacySettingsService: Service {
    
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let urlString = Urls.privacySettingsUrl()
        let requestData = RequestData(url: urlString, headerParams: headerParams, method: HTTPMethod.GET)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
