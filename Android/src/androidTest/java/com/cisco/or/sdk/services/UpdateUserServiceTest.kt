package com.cisco.or.sdk.services

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.mock.mockFunctions
import com.cisco.or.sdk.models.UserDetail
import com.cisco.or.sdk.types.ServiceHandler
import io.mockk.*
import org.junit.Before
import org.junit.Test


internal class UpdateUserServiceTest {
    val accessToken = "fakeToken"
    val mockedContext: Context = mockkClass(Context::class)
    val mockedServiceHandler = mockk<ServiceHandler>()
    val updateUserServiceMock = spyk(UpdateUserService)
    private lateinit var userDetail:UserDetail
    private var mockedSharedPreferences: SharedPreferences = mockkClass(SharedPreferences::class)

    @Before
    fun setUp(){
        MockKAnnotations.init(this)
        mockFunctions(
            mockedSharedPreferences,
            mockedContext,
            accessToken
        )
        this.userDetail = UserDetail("Robert", "", "", 25, "001231548")
    }

    @Test
    fun updateUserWithSucessTest() {
        every { (updateUserServiceMock).call(mockedContext, any(), Service.RESPONSE_FORMATTER.JSON, mockedServiceHandler) } just Runs
        updateUserServiceMock.start(mockedContext, userDetail, "test@test.com", mockedServiceHandler)
        verify { updateUserServiceMock.call(mockedContext, any(), Service.RESPONSE_FORMATTER.JSON, mockedServiceHandler) }
    }
}