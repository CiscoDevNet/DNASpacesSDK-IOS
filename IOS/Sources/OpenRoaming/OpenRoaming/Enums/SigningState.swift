//
//  SigningState.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 10/04/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

public enum SigningState : String {
    case signed
    case unsigned
    
    public var hashValue: Int {
        return self.toInt()
    }
    
    private func toInt() -> Int {
        switch self {
        case .signed:
            return 0
        case .unsigned:
            return 1
        }
    }
    
}
