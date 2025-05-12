package com.aura.ui.transfer

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
 * Unit tests for `TransferViewModel`.
 *
 * Ensures that the transfer logic triggers the correct state transitions
 * and responds correctly to successful transfers.
 */
@ExperimentalCoroutinesApi
class TransferViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private var api = FakeLocalApiService()
    val repository = FakeAuraRepository(api)
    private val viewModel = TransferViewModel(repository)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests that a valid transfer emits `Idle → Loading → Success`.
     */
    @Test
    fun shouldEmitIdleThenLoadingThenSuccess() = runTest {
        viewModel.uiState.test {

            val initial = awaitItem()
            Assert.assertEquals(State.Idle, initial.result)

            viewModel.transferData("5678", "1234", "100")

            val loading = awaitItem()
            Assert.assertEquals(State.Loading, loading.result)

            val success = awaitItem()
            Assert.assertEquals(State.Success, success.result)

            cancelAndIgnoreRemainingEvents()
        }

    }
}