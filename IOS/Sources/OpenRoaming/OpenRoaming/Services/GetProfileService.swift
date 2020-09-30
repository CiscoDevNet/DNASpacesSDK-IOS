//
//  GetProfileService.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 26/04/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import UIKit

/**
* Gets a profile.
*/
class GetProfileService: Service {
    
    /**
    * Method responsible to make the API call that gets a profile.
    *
    * Parameter accessToken: User's access token.
    * Parameter serviceHandler: Type of handler to response.
    */
    override func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let urlString = Urls.installProfileUrl()
        let requestData = RequestData(url: urlString, headerParams: headerParams, method: HTTPMethod.GET)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
