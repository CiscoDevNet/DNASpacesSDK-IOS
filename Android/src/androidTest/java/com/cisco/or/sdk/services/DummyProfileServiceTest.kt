package com.cisco.or.sdk.services

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.mock.mockFunctions
import com.cisco.or.sdk.types.ServiceHandler
import io.mockk.*
import org.junit.Before
import org.junit.Test

internal class DummyProfileServiceTest {
    var accessToken = "fakeToken"
    var mockedContext: Context = mockkClass(Context::class)
    var mockedServiceHandler = mockk<ServiceHandler>()
    var dummyProfileService = spyk(DummyProfileService)

    private var mockedSharedPreferences: SharedPreferences = mockkClass(SharedPreferences::class)

    @Before
    fun setUp(){
        MockKAnnotations.init(this)
        mockFunctions(
            mockedSharedPreferences,
            mockedContext,
            accessToken
        )
    }

    @Test
    fun deleteAccountFromServerApiCallSuccessTest() {
        every { (dummyProfileService).call(mockedContext, any(), Service.RESPONSE_FORMATTER.BINARY, mockedServiceHandler) } just Runs
        dummyProfileService.start(mockedContext, mockedServiceHandler)
        verify { dummyProfileService.call(mockedContext, any(), Service.RESPONSE_FORMATTER.BINARY, mockedServiceHandler) }
    }
}