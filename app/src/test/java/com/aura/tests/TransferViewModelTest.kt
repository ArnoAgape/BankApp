package com.aura.tests

import app.cash.turbine.test
import com.aura.repository.FakeAuraRepository
import com.aura.repository.FakeLocalApiService
import com.aura.ui.states.State
import com.aura.ui.transfer.TransferViewModel
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

@ExperimentalCoroutinesApi
class TransferViewModelTest {

    private lateinit var viewModel: TransferViewModel
    private val testDispatcher = StandardTestDispatcher()
    private var api = FakeLocalApiService()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val repository = FakeAuraRepository(api)
        viewModel = TransferViewModel(repository)
        api = FakeLocalApiService()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun shouldEmitIdleThenLoadingThenSuccess() = runTest {
        viewModel.uiState.test {

            val initial = awaitItem()
            assertEquals(State.Idle, initial.result)

            viewModel.transferData("5678", "1234", "100")

            val loading = awaitItem()
            assertEquals(State.Loading, loading.result)

            val success = awaitItem()
            assertEquals(State.Success, success.result)

            cancelAndIgnoreRemainingEvents()
        }

    }
}