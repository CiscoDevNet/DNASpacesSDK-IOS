//
//  SetPrivacySettingsService.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 06/05/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import UIKit

class SetPrivacySettingsService: Service {
    
    var acceptPrivacySettings:Bool?
    
    /**
    * Not implemented.
    * Use start(context: Context, acceptPrivacySettings: Boolean, serviceHandler: ServiceHandler).
    */
    override func start(serviceHandler: @escaping ServiceHandler) {
        fatalError("Not implemented")
    }
    
    func start(acceptPrivacySettings: Bool, serviceHandler: @escaping ServiceHandler) {
        self.acceptPrivacySettings = acceptPrivacySettings
        super.start(serviceHandler: serviceHandler)
    }
    
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let urlString = Urls.privacySettingsUrl() + "/" + String(acceptPrivacySettings!)
        let requestData = RequestData(url: urlString, headerParams: headerParams, method: HTTPMethod.PUT)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
