package com.aura.ui.login

import app.cash.turbine.test
import com.aura.FakeAuraRepository
import com.aura.FakeLocalApiService
import com.aura.ui.home.HomeViewModel
import com.aura.ui.states.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
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

    private val testDispatcher = UnconfinedTestDispatcher()
    private var api = FakeLocalApiService()
    val repository = FakeAuraRepository(api)
    private val viewModel = LoginViewModel(repository)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
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
            Assert.assertEquals(State.Idle, initial.result)

            viewModel.loginData("5678", "T0pSecr3t")

            val loading = awaitItem()
            Assert.assertEquals(State.Loading, loading.result)

            val success = awaitItem()
            Assert.assertEquals(State.Success, success.result)

            cancelAndIgnoreRemainingEvents()
        }

    }

}