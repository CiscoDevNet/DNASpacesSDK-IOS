package com.cisco.or.sdk.types

import com.cisco.or.sdk.enums.*
import com.cisco.or.sdk.models.User
import com.cisco.or.sdk.models.UsageStatistics
import com.cisco.or.sdk.models.UserDetail
import com.cisco.or.sdk.services.HTTPResponse

typealias InitializeHandler = (signingState: SigningState) -> Unit
typealias SigningHandler = () -> Unit
typealias LoadingHandler = (state: LoadingState) -> Unit
typealias ServiceHandler = (response: HTTPResponse?) -> Unit
typealias PrivacySettingsHandler = () -> Unit
typealias UsageStatisticsHandler = (usageStatiticsData: UsageStatistics) -> Unit
typealias GetPrivacySettingsHandler = (privacySettings: Boolean) -> Unit
typealias ProfileExistenceHandler = (existence: Boolean) -> Unit
typealias UserDetailsHandler = (userDetails: UserDetail) -> Unit
typealias AssociatePushHandler = () -> Unit
typealias UpdateUserHandler = () -> Unit
typealias DeleteUserHandler = () -> Unit
typealias DeleteProfileHandler = () -> Unit
typealias ProvisionProfileHandler = () -> Unit
typealias LocationHandler = (location: Pair<Double?, Double?>) -> Unit
typealias NotificationHandler = (disregardNotification : Boolean, message : String?) -> Unit
typealias RefreshTokenHandler = () -> Unit
