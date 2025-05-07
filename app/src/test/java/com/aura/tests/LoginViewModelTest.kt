package com.aura.tests

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.aura.repository.FakeAuraRepository
import com.aura.repository.FakeLocalApiService
import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.UserModel
import com.aura.ui.home.HomeViewModel
import com.aura.ui.login.LoginViewModel
import com.aura.ui.states.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val fakeStateFlow = MutableStateFlow<State>(State.Idle)
    private var api = FakeLocalApiService()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val repository = FakeAuraRepository()
        viewModel = LoginViewModel(repository)
        api = FakeLocalApiService()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun shouldEmitIdleThenLoadingThenSuccess() = runTest {

        val loginResult = api.login("5678", "T0pSecr3t").first()
        assertTrue(loginResult) // ✅ identifiants corrects

        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(State.Idle, initial.result)

            viewModel.loginData("1234", "p@sswOrd")

            val loading = awaitItem()
            assertEquals(State.Loading, loading.result)

            val success = awaitItem()
            assertEquals(State.Success, success.result)

            cancelAndIgnoreRemainingEvents()
        }

    }

}