//
//  DeleteAccountService.swift
//  OpenRoaming
//
//  Created by Fabiana Garcia on 5/22/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class DeleteAccountServiceV1: Service {
    
    /**
    * Method responsible to delete an account from the app.
    *
    * Parameter accessToken: A valid user token for the operation
    * Parameter serviceHandler: Type of handler to response
    */
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let urlString = Urls.deleteAccountUrl(accessToken: SharedPrefs().sdkToken!)
        let requestData = RequestData(url: urlString, headerParams: headerParams, method: HTTPMethod.DELETE)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
