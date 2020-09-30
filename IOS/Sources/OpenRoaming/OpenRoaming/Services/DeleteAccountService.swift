//
//  DeleteAccountService.swift
//  OpenRoaming
//
//  Created by olivier duque campos on 23/06/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class DeleteAccountService: Service {
    
    /**
    * Method responsible to delete an account on openroaming backend.
    */
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let urlString = Urls.userDeleteAccountUrl()
        let requestData = RequestData(url: urlString, headerParams: headerParams, method: HTTPMethod.DELETE)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
