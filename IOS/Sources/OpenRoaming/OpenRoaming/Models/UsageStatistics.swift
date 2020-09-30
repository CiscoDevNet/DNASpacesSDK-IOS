//
//  Stats.swift
//  OpenRoaming
//
//  Created by Fabiana Garcia on 4/27/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

public struct UsageStatistics {
    public private(set) var usageStatistics = Array<Stats>()
    
    init(width data: Data) {
        do {
            let jsonArray = try (JSONSerialization.jsonObject(with: data, options: .mutableContainers) as! Array<NSDictionary>)
            
            for item in jsonArray {
                if (item.count > 0) {
                    let stats = Stats(item)
                    self.usageStatistics.append(stats)
                }
            }
        }
        catch {
            print(error)
        }
    }
}

public struct Stats {
    public let startTime: Int64
    public let endTime: Int64
    public let ssid: String
    public let device: String
    public let duration: Int64
    public let dateTime: String
    
    init(_ jsonResult: NSDictionary) {
        self.startTime = jsonResult["starttime"] as! Int64
        self.endTime = jsonResult["endtime"] as! Int64
        self.ssid = jsonResult["ssid"] as! String
        self.device = jsonResult["device"] as! String
        self.duration = jsonResult["duration"] as! Int64
        self.dateTime = jsonResult["datetime"] as! String
    }
}
