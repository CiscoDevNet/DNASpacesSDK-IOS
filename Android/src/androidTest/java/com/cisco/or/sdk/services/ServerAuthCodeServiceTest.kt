package com.cisco.or.sdk.services

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.enums.SigningServiceName
import com.cisco.or.sdk.mock.mockFunctions
import com.cisco.or.sdk.types.ServiceHandler
import io.mockk.*
import org.junit.Before
import org.junit.Test


internal class ServerAuthCodeServiceTest {
    val accessToken = "fakeToken"
    val mockedContext: Context = mockkClass(Context::class)
    val mockedServiceHandler = mockk<ServiceHandler>()
    val oAuthTokenServiceMocked = spyk(ServerAuthCodeService)
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
    fun callOAuthTokenTest(){
        every { (oAuthTokenServiceMocked).call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) } just Runs
        oAuthTokenServiceMocked.start(mockedContext, "QASC41x547454q", SigningServiceName.APPLE.value, mockedServiceHandler)
        verify { oAuthTokenServiceMocked.call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) }
    }
}