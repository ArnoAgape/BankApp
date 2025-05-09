package com.aura.tests

import app.cash.turbine.test
import com.aura.repository.FakeAuraClient
import com.aura.repository.FakeAuraRepositoryNoNetwork
import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.TransferModel
import com.aura.ui.home.HomeResponse
import com.aura.ui.login.LoginResponse
import com.aura.ui.states.errors.UnknownUserException
import com.aura.ui.transfer.TransferResponse
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertTrue

/**
 * Unit tests for `AuraRepository` using a fake implementation of `AuraClient`.
 *
 * These tests verify that:
 * - Login, user data, and transfer operations behave as expected
 * - Proper exceptions are thrown for known server errors
 *
 * Uses `FakeAuraRepositoryNoNetwork` which skips network checking logic
 * and focuses on repository behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuraRepositoryTest {

    /**
     * Verifies that a successful login returns `true`.
     */
    @Test
    fun fetchLoginData_shouldEmitTrue_whenLoginSucceeds() = runTest {
        val fakeClient = object : FakeAuraClient() {
            override suspend fun loginDetails(request: LoginModel): LoginResponse {
                return LoginResponse(true)
            }
        }

        val repo = FakeAuraRepositoryNoNetwork(fakeClient) // version sans NetworkStatusChecker

        repo.fetchLoginData("1234", "p@sswOrd").test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    /**
     * Verifies that user account data is fetched and mapped correctly.
     */
    @Test
    fun fetchUserData_shouldEmitTheListOfUser() = runTest {
        val fakeClient = object : FakeAuraClient() {
            override suspend fun userId(id: String): List<HomeResponse> {
                return listOf(HomeResponse("1234", true, 100.0))
            }
        }

        val repo = FakeAuraRepositoryNoNetwork(fakeClient) // version sans NetworkStatusChecker

        repo.fetchUserData("1234").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("1234", result[0].id)
            assertEquals(100.0, result[0].balance, 0.01)
            assertTrue(result[0].main)
            awaitComplete()
        }
    }

    /**
     * Verifies that a successful transfer returns `true`.
     */
    @Test
    fun fetchTransferData_shouldEmitTrue_whenTransferSucceeds() = runTest {
        val fakeClient = object : FakeAuraClient() {
            override suspend fun transferDetails(request: TransferModel): Response<TransferResponse> {
                return Response.success(TransferResponse(true))
            }
        }

        val repo = FakeAuraRepositoryNoNetwork(fakeClient) // version sans NetworkStatusChecker

        repo.fetchTransferData("1234", "5678", 100.0).test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    /**
     * Verifies that a server error with status 500 triggers `UnknownUserException`.
     */
    @Test
    fun fetchTransferData_shouldThrowUnknownUserException_whenServerReturns500() = runTest {

        val fakeClient = object : FakeAuraClient() {
            override suspend fun transferDetails(request: TransferModel): Response<TransferResponse> {
                return Response.error(500, "".toResponseBody("application/json".toMediaType()))
            }
        }

        val repo = FakeAuraRepositoryNoNetwork(fakeClient)

        repo.fetchTransferData("1234", "8888", 100.0).test {
            val error = awaitError()
            assertTrue(error is UnknownUserException)
        }
    }
}