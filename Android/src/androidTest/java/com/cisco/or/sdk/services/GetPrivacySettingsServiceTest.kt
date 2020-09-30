package com.cisco.or.sdk.services

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.mock.mockFunctions
import com.cisco.or.sdk.types.ServiceHandler
import io.mockk.*
import org.junit.Before
import org.junit.Test

class GetPrivacySettingsServiceTest{
    private var accessToken = "fakeToken"
    var mockedContext: Context = mockkClass(Context::class)
    var mockedServiceHandler = mockk<ServiceHandler>()
    private var mockedSharedPreferences: SharedPreferences = mockkClass(SharedPreferences::class)
    private var getPrivacySettingsService = spyk(GetPrivacySettingsService)

    @Before
    fun setUp(){
        MockKAnnotations.init(this)
    }

    @Test
    fun getPrivacySettingsServiceSucessTest() {
        mockFunctions(
            mockedSharedPreferences,
            mockedContext,
            accessToken
        )
        every { getPrivacySettingsService.call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) } just Runs
        getPrivacySettingsService.start(mockedContext,mockedServiceHandler)
        verify { getPrivacySettingsService.call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) }
    }
}