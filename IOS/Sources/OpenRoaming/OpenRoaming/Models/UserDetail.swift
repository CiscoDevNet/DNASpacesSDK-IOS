//
//  UserDetail.swift
//  OpenRoaming
//
//  Created by olivier duque campos on 17/06/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation
import UIKit

public struct UserDetail {
    
    public var name: String
    public var phone: String
    public var email: String
    public var age: Int64
    public var zipCode: String

    public init(data: Data) throws {
        let jsonResult: NSDictionary = try (JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? NSDictionary)!
        self.name = jsonResult["name"] as? String ?? "Unknown name"
        self.phone = jsonResult["phone"] as? String ?? ""
        self.email = jsonResult["email"] as? String ?? ""
        self.age = jsonResult["age"] as? Int64 ?? 0
        self.zipCode = jsonResult["zip"] as? String ?? ""
    }
    
    public func json() -> Data {
        let jsonValues: NSMutableDictionary = NSMutableDictionary()
        jsonValues.setValue(self.name, forKey: "name")
        jsonValues.setValue(self.email, forKey: "email")
        jsonValues.setValue(self.phone, forKey: "phone")
        jsonValues.setValue(self.age, forKey: "age")
        jsonValues.setValue(self.zipCode, forKey: "zip")
        
        return try! JSONSerialization.data(withJSONObject: jsonValues)
    }
}
