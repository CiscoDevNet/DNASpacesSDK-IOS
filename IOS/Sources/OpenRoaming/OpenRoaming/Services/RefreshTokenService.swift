//
//  RefreshTokenService.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 05/06/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class RefreshTokenService: RegisterService {
    
    private var registerSdkHandler: RegisterSdkHandler!
    
    /**
    * Not implemented.
    * Use start(context: Context, acceptPrivacySettings: Boolean, serviceHandler: ServiceHandler).
    */
    override func start(serviceHandler: @escaping ServiceHandler) {
        fatalError("Not implemented")
    }
    
    override func start(handler: @escaping RegisterSdkHandler) {
        self.registerSdkHandler = handler
        onStart(headerParams: buildHeaderParams(sdkToken: SharedPrefs().sdkToken!), serviceHandler: {_,_,_ in })
   }

   override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
       let url = Urls.refreshTokenUrl()
       let requestData = RequestData(url: url, headerParams: headerParams, method: HTTPMethod.GET, body: nil)
       call(requestData: requestData, serviceHandler: { data, response, error in
            self.readSdkToken(data, response, error, self.registerSdkHandler)
       })
   }
}
