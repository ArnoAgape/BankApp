package com.aura.tests

import app.cash.turbine.turbineScope
import com.aura.repository.FakeAuraRepository
import com.aura.ui.login.LoginViewModel
import com.aura.ui.states.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val fakeStateFlow = MutableStateFlow<State>(State.Idle)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val fakeRepository = FakeAuraRepository()
        viewModel = LoginViewModel(fakeRepository)
    }

    @Test
    fun onLoginFieldsChanged() = runTest {

        turbineScope {
            val turbine = viewModel.uiState.testIn(backgroundScope)
        }
    }

    @Test
    fun loginData() = runTest {

        turbineScope {
            val turbine = viewModel.uiState.testIn(backgroundScope)
        }
    }

}