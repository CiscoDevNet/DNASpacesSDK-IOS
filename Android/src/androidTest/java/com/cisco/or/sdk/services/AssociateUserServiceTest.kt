package com.cisco.or.sdk

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.enums.IdType
import com.cisco.or.sdk.mock.mockFunctions
import com.cisco.or.sdk.services.AssociateUserService
import com.cisco.or.sdk.services.Service
import com.cisco.or.sdk.types.ServiceHandler
import io.mockk.*
import org.junit.Before
import org.junit.Test


internal class AssociateUserServiceTest {
    val accessToken = "fakeToken"
    val mockedContext: Context = mockkClass(Context::class)
    val mockedServiceHandler = mockk<ServiceHandler>()
    val associateUserServiceMock = spyk(AssociateUserService)
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
    fun associateUserServerApiCallSuccessTest() {
        every { (associateUserServiceMock).call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) } just Runs
        associateUserServiceMock.start(mockedContext, IdType.EMAIL, "email@email.com", mockedServiceHandler)
        verify { associateUserServiceMock.call(mockedContext, any(), Service.RESPONSE_FORMATTER.TEXT, mockedServiceHandler) }
    }
}