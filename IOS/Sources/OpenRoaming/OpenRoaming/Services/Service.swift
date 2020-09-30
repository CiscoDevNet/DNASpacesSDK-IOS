//
//  Service.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 26/04/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import UIKit

class Service {
    
    func start(serviceHandler: @escaping ServiceHandler) {
        let sdkToken = SharedPrefs().sdkToken
        if sdkToken == nil {
            serviceHandler(nil, nil, OpenRoamingError.TokenEmpty)
        }
        else {
            let jwt = JWTDecoder(sdkToken!)
            if (!jwt.expired()){
                onStart(headerParams: buildHeaderParams(sdkToken: sdkToken!), serviceHandler: serviceHandler)
            }
            else{
                RefreshTokenService().start(handler: { _, error in
                    if error == nil {
                        self.onStart(headerParams: self.buildHeaderParams(sdkToken: sdkToken!), serviceHandler: serviceHandler)
                    }
                    else {
                        serviceHandler(nil, nil, OpenRoamingError.TokenEmpty)
                    }
                })
            }
        }
    }
    
    func onStart(headerParams: Array<HeaderParam>, serviceHandler: @escaping ServiceHandler){
        fatalError("This method must be overridden")
    }

    func call(requestData: RequestData, serviceHandler: @escaping ServiceHandler){
        var urlRequest: URLRequest
        
        if let url = URL(string: requestData.url) {
            urlRequest = URLRequest(url: url)
            urlRequest.httpMethod = requestData.method.rawValue
            urlRequest.cachePolicy = .reloadIgnoringLocalCacheData
            
            for param in requestData.headerParams {
                urlRequest.addValue(param.value, forHTTPHeaderField: param.name)
            }
            
            if let strBody = requestData.body {
                urlRequest.httpBody = strBody.data(using: .utf8)
            }
            
            let task = URLSession.shared.dataTask(with: urlRequest, completionHandler: {
                data, response, error in
                guard let response = response else {
                    print("Cannot found the response")
                    print(error ?? "")
                    serviceHandler(nil, nil, OpenRoamingError.ServiceBadResponse)
                    return
                }
                let myResponse = response as! HTTPURLResponse
               
                if error != nil || myResponse.statusCode != 200 {
                    print("Request error:", myResponse.statusCode, requestData.url)
                    serviceHandler(data, response, OpenRoamingError.ServiceBadResponse)
                }
                
                else{
                    serviceHandler(data, response, nil)
                }
            })
            task.resume()
            
        }
        else {
            serviceHandler(nil, nil, OpenRoamingError.ServiceBadResponse)
        }
    }
    
    func guid() -> String {
        return "apple1"
    }
    
    func appId() -> String {
        return SharedPrefs().appId!
    }
    
    func dnaSpacesKey() -> String {
        return SharedPrefs().dnaSpacesKey!
    }
    
    func buildHeaderParams(sdkToken: String) -> Array<HeaderParam> {
        var list  = Array<HeaderParam>()
        list.append(HeaderParam(name: "Content-Type", value: "application/json"))
        list.append(HeaderParam(name: "Authorization", value: sdkToken))
        return list
    }
    
    enum HTTPMethod: String {
        case POST
        case DELETE
        case GET
        case PUT
    }
    
    struct RequestData {
        var url: String
        var headerParams:Array<HeaderParam>
        var method: HTTPMethod
        var body: String?
        
        init(url: String, headerParams:Array<HeaderParam>, method: HTTPMethod, body: String? = nil){
            self.url = url
            self.headerParams = headerParams
            self.method = method
            self.body = body
        }
    }
    
    struct HeaderParam {
        var name: String
        var value: String
    }

}
