package com.aura.tests

import app.cash.turbine.test
import com.aura.repository.FakeAuraRepository
import com.aura.repository.FakeLocalApiService
import com.aura.ui.home.HomeViewModel
import com.aura.ui.states.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import kotlin.test.DefaultAsserter.fail

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private var api = FakeLocalApiService()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val repository = FakeAuraRepository(api)
        viewModel = HomeViewModel(repository)
        api = FakeLocalApiService()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getUserId_with1234_shouldEmitSuccessWithCorrectBalance() = runTest {

        viewModel.uiState.test {

            val initial = awaitItem()
            assertEquals(State.Idle, initial.result)

            viewModel.getUserId("5678")

            val loading = awaitItem()
            assertEquals(State.Loading, loading.result)

            val success = awaitItem()
            assertEquals(State.Success, success.result)

            val mainAccount = success.balance.find { it.main }
            assertNotNull(mainAccount)
            mainAccount?.let {
                assertEquals(10032.21, it.balance, 0.01)
            } ?: fail("mainAccount was null")

            cancelAndIgnoreRemainingEvents()
        }
    }
}
