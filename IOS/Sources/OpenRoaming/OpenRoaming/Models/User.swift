//
//  User.swift
//  OpenRoaming
//
//  Created by Fabiana Garcia on 4/29/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation
import UIKit

public class User {
    var devicesList: Array<DeviceData> = Array()

    public let userId: String
    public let oauthIds: Array<String>
    public let devices: Array<DeviceData>
    public var details: UserDetail?
    public let userIdentity: String

    public let serviceName: ServiceName
    public let name: String
    public let imageName: String

    init(data: Data, userDetail: Data? = nil) throws {
                
        let jsonResult: NSDictionary = try (JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? NSDictionary)!
        let dataJson = jsonResult["data"] as! NSDictionary
        self.userId = dataJson["user_id"] as! String
        self.oauthIds = dataJson["oauth_ids"] as! Array<String>
        let tokensJson = dataJson["tokens"] as! NSDictionary
        
        for key in tokensJson.allKeys {
            let value = tokensJson[key]
            self.devicesList.append(DeviceData(key: key as! String, value: value as! NSDictionary))
        }
        
        self.devices = devicesList

        if let userDetail = userDetail {
            self.details = try (UserDetail(data: userDetail))
        } else {
            self.details = nil
        }

        self.userIdentity = self.oauthIds[0]

        self.serviceName = .google
        self.name = ""
        self.imageName = ""
    }
}
