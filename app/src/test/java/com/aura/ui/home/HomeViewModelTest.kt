package com.aura.ui.home

import app.cash.turbine.test
import com.aura.FakeAuraRepository
import com.aura.FakeLocalApiService
import com.aura.ui.states.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.DefaultAsserter

/**
 * Unit tests for `HomeViewModel` using a `FakeAuraRepository` and `FakeLocalApiService`.
 *
 * These tests ensure that:
 * - The ViewModel emits the correct sequence of states: Idle → Loading → Success
 * - The balance and main account information is correctly retrieved and validated
 */
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private var api = FakeLocalApiService()
    val repository = FakeAuraRepository(api)
    private val viewModel = HomeViewModel(repository)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests that calling `getUserId` with a valid ID emits `Success`
     * and that the main account has the expected balance.
     */
    @Test
    fun getUserId_with1234_shouldEmitSuccessWithCorrectBalance() = runTest {

        viewModel.uiState.test {

            val initial = awaitItem()
            Assert.assertEquals(State.Idle, initial.result)

            viewModel.getUserId("1234")

            val loading = awaitItem()
            Assert.assertEquals(State.Loading, loading.result)

            val success = awaitItem()
            Assert.assertEquals(State.Success, success.result)

            val mainAccount = success.balance.find { it.main }
            Assert.assertNotNull(mainAccount)
            mainAccount?.let {
                Assert.assertEquals(2354.23, it.balance, 0.01)
            } ?: DefaultAsserter.fail("mainAccount was null")

            cancelAndIgnoreRemainingEvents()
        }
    }
}