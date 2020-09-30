package com.cisco.or.sdk.services

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.mock.mockFunctions
import com.cisco.or.sdk.types.ServiceHandler
import io.mockk.*
import org.junit.Before
import org.junit.Test


internal class PushIdentifierServiceTest {
    val accessToken = "fakeToken"
    val mockedContext: Context = mockkClass(Context::class)
    val mockedServiceHandler = mockk<ServiceHandler>()
    val pushIdentifierServiceMock = spyk(PushIdentifierService)
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

    @Test(expected = NotImplementedError::class)
    fun callPushIdentifierWithStartNotImplemetedTest() {
        pushIdentifierServiceMock.start(mockedContext, mockedServiceHandler)
    }

    @Test
    fun callPushIdentifierTest(){
        every { (pushIdentifierServiceMock).call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) } just Runs
        pushIdentifierServiceMock.start(mockedContext, true, "push_token", mockedServiceHandler)
        verify { pushIdentifierServiceMock.call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) }
    }
}