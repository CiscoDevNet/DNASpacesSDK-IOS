package com.cisco.or.sdk.services

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.mock.mockFunctions
import com.cisco.or.sdk.types.ServiceHandler
import io.mockk.*
import org.junit.Before
import org.junit.Test


internal class RegisterTokenServiceTest {
    val accessToken = "fakeToken"
    val mockedContext: Context = mockkClass(Context::class)
    val mockedServiceHandler = mockk<ServiceHandler>()
    val registerTokenMock = spyk(RegisterService)
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
    fun refreshTokenCallWithSuccessTest() {
        every { (registerTokenMock).call(mockedContext, any(), Service.RESPONSE_FORMATTER.JSON, mockedServiceHandler) } just Runs
        registerTokenMock.start(mockedContext, mockedServiceHandler)
        verify { registerTokenMock.call(mockedContext, any(), Service.RESPONSE_FORMATTER.JSON, mockedServiceHandler) }
    }
}