package com.aura.tests

import app.cash.turbine.test
import com.aura.repository.FakeAuraRepository
import com.aura.repository.FakeLocalApiService
import com.aura.ui.login.LoginViewModel
import com.aura.ui.states.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for `LoginViewModel`.
 *
 * Verifies the expected state flow:
 * - From Idle to Loading to Success
 * when login credentials are valid.
 */
@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()
    private var api = FakeLocalApiService()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val repository = FakeAuraRepository(api)
        viewModel = LoginViewModel(repository)
        api = FakeLocalApiService()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests that login emits `Idle → Loading → Success` with valid credentials.
     */
    @Test
    fun shouldEmitIdleThenLoadingThenSuccess() = runTest {
        viewModel.uiState.test {

            val initial = awaitItem()
            assertEquals(State.Idle, initial.result)

            viewModel.loginData("5678", "T0pSecr3t")

            val loading = awaitItem()
            assertEquals(State.Loading, loading.result)

            val success = awaitItem()
            assertEquals(State.Success, success.result)

            cancelAndIgnoreRemainingEvents()
        }

    }

}