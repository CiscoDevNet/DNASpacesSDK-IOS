//
//  UpdateUserService.swift
//  OpenRoaming
//
//  Created by Fabiana Garcia on 6/22/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

class UpdateUserService : Service {
    
    private var userDetail: UserDetail?
    
    override func start(serviceHandler: @escaping ServiceHandler) {
        fatalError("Not implemented")
    }
    
    func start(userDetail: UserDetail, serviceHandler: @escaping ServiceHandler) {
        self.userDetail = userDetail
        super.start(serviceHandler: serviceHandler)
    }
    
    override func onStart(headerParams: Array<Service.HeaderParam>, serviceHandler: @escaping ServiceHandler) {
        let url = Urls.updateUserUrl()
        
        let body = String(data: userDetail?.json() ?? Data(), encoding: .utf8)
        
        let requestData = RequestData(url: url, headerParams: headerParams, method: HTTPMethod.POST, body: body)
        call(requestData: requestData, serviceHandler: serviceHandler)
    }
}
