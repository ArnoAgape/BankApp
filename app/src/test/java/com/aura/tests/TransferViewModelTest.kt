package com.aura.tests

import app.cash.turbine.turbineScope
import com.aura.repository.FakeAuraRepository
import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.TransferModel
import com.aura.ui.domain.model.UserModel
import com.aura.ui.states.State
import com.aura.ui.transfer.TransferViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TransferViewModelTest {

    private lateinit var viewModel: TransferViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val fakeStateFlow = MutableStateFlow<State>(State.Idle)

    val isTransferEnabled: Boolean = false
    val sameUserId: Boolean = false

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val fakeRepository = FakeAuraRepository()
        viewModel = TransferViewModel(fakeRepository)
    }

    @Test
    fun onLoginFieldsChanged() = runTest {

        turbineScope {
            val turbine = viewModel.uiState.testIn(backgroundScope)

            assertEquals(emptyList<UserModel>(), turbine.awaitItem().isTransferEnabled)

        }
    }

    @Test
    fun transferData() = runTest {

        turbineScope {
            val turbine = viewModel.uiState.testIn(backgroundScope)

            assertEquals(emptyList<UserModel>(), turbine.awaitItem().result)

            val balanceUser1 = UserModel("1", true, 500.0)
            val balanceUser2 = UserModel("2", true, 1000.0)
            val user1 = LoginModel("id1", "")
            val user2 = LoginModel("id2", "")
            val transfer1 = TransferModel(
                LoginModel("id1", "password").toString(),
                LoginModel("id2", "password").toString(),
                100.0)
            fakeStateFlow.value = listOf(balanceUser1, balanceUser2) as State

            val actualBalance = turbine.awaitItem().result

            turbine.cancelAndIgnoreRemainingEvents()

        }
    }
}