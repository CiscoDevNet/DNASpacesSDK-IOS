//
//  Types.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 15/04/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import Foundation

public typealias RegisterSdkHandler = (SigningState?, OpenRoamingError?) -> Void
public typealias ProvisionProfileHandler = (OpenRoamingError?) -> Void
public typealias SigningHandler = (OpenRoamingError?) -> Void
public typealias ServiceHandler = (Data?, URLResponse?, OpenRoamingError?) -> Void
public typealias UsageStatisticsHandler = (UsageStatistics?, OpenRoamingError?) -> Void
public typealias UserDetailsHandler = (UserDetail?, OpenRoamingError?) -> Void
public typealias UserAccountDetailsHandler = (UserDetail?, OpenRoamingError?) -> Void
public typealias UpdateUserHandler = (OpenRoamingError?) -> Void
public typealias ProfileExistenceHandler = (Bool?, OpenRoamingError?) -> Void
public typealias PrivacySettingsHandler = (Bool?, OpenRoamingError?) -> Void
public typealias GetPrivacySettingsHandler = (Bool?, OpenRoamingError?) -> Void
public typealias AssociatePushHandler = (OpenRoamingError?) -> Void
public typealias DeleteUserHandler = (OpenRoamingError?) -> Void
public typealias DeleteProfileHandler = (OpenRoamingError?) -> Void
public typealias NotificationHandler = (Bool?, String?, OpenRoamingError?) -> Void
public typealias LocationHandler = ([(Double?, Double?)], OpenRoamingError?) -> Void
