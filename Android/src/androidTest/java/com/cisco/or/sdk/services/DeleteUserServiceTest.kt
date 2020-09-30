package com.cisco.or.sdk.services

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.mock.mockFunctions
import com.cisco.or.sdk.types.ServiceHandler
import io.mockk.*
import org.junit.Before
import org.junit.Test


internal class DeleteUserServiceTest {
    val accessToken = "fakeToken"
    val mockedContext: Context = mockkClass(Context::class)
    val mockedServiceHandler = mockk<ServiceHandler>()
    val deleteUserServiceMock = spyk(DeleteUserService)
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
    fun deleteUserServerApiCallSuccessTest() {
        every { (deleteUserServiceMock).call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) } just Runs
        deleteUserServiceMock.start(mockedContext, mockedServiceHandler)
        verify { deleteUserServiceMock.call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) }
    }
}