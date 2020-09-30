package com.cisco.or.sdk.services

import android.content.Context
import android.content.SharedPreferences
import com.cisco.or.sdk.mock.mockFunctions
import com.cisco.or.sdk.types.ServiceHandler
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

/**
 * Unit test class for UsageStatisticsService.kt
 * @author Fabiana Garcia
 */

class UsageStatisticsServiceTest {

    var accessToken = "fakeToken"
    var mockedContext: Context = mockkClass(Context::class)
    var mockedServiceHandler = mockk<ServiceHandler>()
    private var mockedSharedPreferences: SharedPreferences = mockkClass(SharedPreferences::class)
    private var usageStatisticsService = spyk(UsageStatisticsService)

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun shouldReturnValidListWhenApiIsSuccessfullyCalled(){
        mockFunctions(
            mockedSharedPreferences,
            mockedContext,
            accessToken
        )
        every {usageStatisticsService.call(mockedContext, any(), Service.RESPONSE_FORMATTER.JSON, mockedServiceHandler)} just Runs
        usageStatisticsService.start(mockedContext, mockedServiceHandler)
        verify { usageStatisticsService.call(mockedContext, any(), Service.RESPONSE_FORMATTER.JSON, mockedServiceHandler) }
    }
}