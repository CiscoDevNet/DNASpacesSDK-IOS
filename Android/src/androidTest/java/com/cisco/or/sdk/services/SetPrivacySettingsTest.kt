package com.cisco.or.sdk.services

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.mock.mockFunctions
import com.cisco.or.sdk.types.ServiceHandler
import com.cisco.or.sdk.utils.Urls
import io.mockk.*
import org.junit.Before
import org.junit.Test

class SetPrivacySettingsTest {


    private var accessToken = "fakeToken"
    var mockedContext: Context = mockkClass(Context::class)
    var mockedServiceHandler = mockk<ServiceHandler>()
    private var mockedSharedPreferences: SharedPreferences = mockkClass(SharedPreferences::class)
    private var mockedSetPrivacySettingsUrl = Urls.privacySettingsUrl()
    private var setPrivacySettingsService = spyk(SetPrivacySettingsService)

    @Before
    fun setUp(){
        MockKAnnotations.init(this)
    }

    @Test(expected = NotImplementedError::class)
    fun setPrivacySettingsTrueParameterNotImplementedTest() {
        setPrivacySettingsService.start(mockedContext,mockedServiceHandler)
    }

    @Test
    fun setPrivacySettingsUrlParameterSucessTest() {
        mockFunctions(
            mockedSharedPreferences,
            mockedContext,
            accessToken
        )
        every { setPrivacySettingsService.call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) } just Runs
        setPrivacySettingsService.start(mockedContext,true,mockedServiceHandler)
        verify {setPrivacySettingsService.call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) }
    }
}