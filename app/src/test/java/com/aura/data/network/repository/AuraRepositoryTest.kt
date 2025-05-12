package com.aura.data.network.repository

import app.cash.turbine.test
import com.aura.ui.data.network.repository.AuraRepository
import com.aura.ui.domain.model.UserModel
import com.aura.ui.states.errors.UnknownUserException
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

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

    @MockK
    private var repo = mockk<AuraRepository>()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    /**
     * Verifies that a successful login returns `true`.
     */
    @Test
    fun fetchLoginData_shouldEmitTrue_whenLoginSucceeds() = runTest {

        coEvery { repo.fetchLoginData("1234", "p@sswOrd") }.returns(flow { emit(true) })

        repo.fetchLoginData("1234", "p@sswOrd").test {
            assertTrue(awaitItem())
            awaitComplete()
        } // returns true

        coVerify(exactly = 1) { repo.fetchLoginData("1234", "p@sswOrd") }

        confirmVerified(repo)
    }

    /**
     * Verifies that user account data is fetched and mapped correctly.
     */
    @Test
    fun fetchUserData_shouldEmitTheListOfUser() = runTest {

        val expectedModel = listOf(
            UserModel(id = "1234", main = true, balance = 100.0)
        )

        coEvery { repo.fetchUserData("1234") }.returns(flow {
            emit(expectedModel)
        })

        repo.fetchUserData("1234").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("1234", result[0].id)
            assertTrue(result[0].main)
            assertEquals(100.0, result[0].balance, 0.01)
            awaitComplete()
        } // returns model

        coVerify { repo.fetchUserData("1234") }

        confirmVerified(repo)

    }

    /**
     * Verifies that a successful transfer returns `true`.
     */
    @Test
    fun fetchTransferData_shouldEmitTrue_whenTransferSucceeds() = runTest {

        coEvery { repo.fetchTransferData("1234", "5678", 100.0) }.returns(flow { emit(true) })

        repo.fetchTransferData("1234", "5678", 100.0).test {
            assertTrue(awaitItem())
            awaitComplete()
        } // returns true

        coVerify(exactly = 1) { repo.fetchTransferData("1234", "5678", 100.0) }

        confirmVerified(repo)
    }

    /**
     * Verifies that a server error with status 500 triggers `UnknownUserException`.
     */
    @Test
    fun fetchTransferData_shouldThrowUnknownUserException() = runTest {

        coEvery { repo.fetchTransferData("1234", "8888", 100.0) }.throws(UnknownUserException())

        assertFailsWith<UnknownUserException> {
            repo.fetchTransferData("1234", "8888", 100.0)
        }
    }
}