package com.aura.data.network.repository

import app.cash.turbine.test
import com.aura.ui.data.network.AuraClient
import com.aura.ui.data.network.repository.AuraRepository
import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.TransferModel
import com.aura.ui.domain.model.UserModel
import com.aura.ui.home.HomeResponse
import com.aura.ui.login.LoginResponse
import com.aura.ui.states.errors.NetworkStatusChecker
import com.aura.ui.states.errors.UnknownUserException
import com.aura.ui.transfer.TransferResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.test.assertFailsWith

/**
 * Unit tests for `AuraRepository` using a fake implementation of `AuraClient`.
 *
 * These tests verify that:
 * - Login, user data, and transfer operations behave as expected
 * - Proper exceptions are thrown for known server errors
 *
 * Mocks `AuraRepository`
 * and focuses on repository behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuraRepositoryTest {

    private val networkStatusChecker = mockk<NetworkStatusChecker>()
    private val dataService = mockk<AuraClient>()

    val repo = AuraRepository(networkStatusChecker, dataService)


    /**
     * Verifies that a successful login returns `true`.
     */
    @Test
    fun fetchLoginData_shouldEmitTrue_whenLoginSucceeds() = runTest {

        coEvery { dataService.loginDetails(LoginModel(id="1234", password="p@sswOrd")) }
            .returns(LoginResponse(granted = true))

        repo.fetchLoginData("1234", "p@sswOrd").test {
            assertTrue(awaitItem())
            awaitComplete()
        } // returns true

        coVerify(exactly = 1) { dataService.loginDetails(LoginModel(id="1234", password="p@sswOrd")) }

        confirmVerified(dataService)
    }

    /**
     * Verifies that user account data is fetched and mapped correctly.
     */
    @Test
    fun fetchUserData_shouldEmitTheListOfUser() = runTest {

        coEvery { dataService.userId("1234") }
            .returns(listOf(HomeResponse(id = "1234", main = true, balance = 100.0)))

        repo.fetchUserData("1234").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("1234", result[0].id)
            assertTrue(result[0].main)
            assertEquals(100.0, result[0].balance, 0.01)
            awaitComplete()
        } // returns model

        coVerify { dataService.userId("1234") }

        confirmVerified(dataService)

    }

    /**
     * Verifies that a successful transfer returns `true`.
     */
    @Test
    fun fetchTransferData_shouldEmitTrue_whenTransferSucceeds() = runTest {

        coEvery { dataService.transferDetails(TransferModel(sender = "1234", recipient = "5678", amount = 100.0)) }
            .returns(Response.success(TransferResponse(result = true)))

        repo.fetchTransferData(sender = "1234", recipient = "5678", amount = 100.0).test {
            awaitItem()
            assertTrue(awaitItem())
            awaitComplete()
        } // returns true

        coVerify(exactly = 1) { dataService.transferDetails(TransferModel(sender = "1234", recipient = "5678", amount = 100.0)) }

        confirmVerified(dataService)
    }

    /**
     * Verifies that a server error with status 500 triggers `UnknownUserException`.
     */
    @Test
    fun fetchTransferData_shouldThrowUnknownUserException() = runTest {

        coEvery { dataService.transferDetails(TransferModel("1234", "8888", 100.0)) }.throws(UnknownUserException())

        assertFailsWith<UnknownUserException> {
            repo.fetchTransferData("1234", "8888", 100.0).first()
        }
    }
}