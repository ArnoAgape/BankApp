package com.aura.tests

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.aura.repository.FakeAuraClient
import com.aura.repository.FakeAuraRepository
import com.aura.repository.FakeLocalApiService
import com.aura.ui.data.network.repository.AuraRepository
import com.aura.ui.data.network.repository.AuraRepositoryInterface
import com.aura.ui.domain.model.UserModel
import com.aura.ui.home.HomeViewModel
import com.aura.ui.states.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.exp

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private var api = FakeLocalApiService()
    private val fakeClient = FakeAuraClient()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val repository = FakeAuraRepository()
        viewModel = HomeViewModel(repository)
        api = FakeLocalApiService()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getUserId_with1234_shouldEmitSuccessWithCorrectBalance() = runTest {
        val fakeRepo = FakeAuraRepository().apply {
            setShouldReturnError(false)
        }

        val viewModel = HomeViewModel(fakeRepo)

        viewModel.uiState.test {
            awaitItem() // État initial : Idle

            viewModel.getUserId("1234")

            val loading = awaitItem()
            assertEquals(State.Loading, loading.result)

            val success = awaitItem()
            assertEquals(State.Success, success.result)

            cancelAndIgnoreRemainingEvents()
        }
    }



}
