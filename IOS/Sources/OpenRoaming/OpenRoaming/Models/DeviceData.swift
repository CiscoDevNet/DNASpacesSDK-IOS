//
//  DeviceData.swift
//  OpenRoaming
//
//  Created by Fabiana Garcia on 4/29/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation
import UIKit

public class DeviceData {
    public let name: String
    public let token: String
    public let deviceType: String
    public let uniqueDeviceId: String
    public let key: String
    
    public init(key: String, value: NSDictionary) {
        self.name = value["name"] as! String
        self.token = value["token"] as! String
        self.deviceType = value["deviceType"] as! String
        self.uniqueDeviceId = value["uniqueDeviceId"] as! String
        self.key = key
    }
}
