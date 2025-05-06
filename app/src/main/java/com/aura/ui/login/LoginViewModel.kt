package com.aura.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.R
import com.aura.ui.data.network.repository.AuraRepositoryInterface
import com.aura.ui.states.errors.NoConnectionException
import com.aura.ui.states.errors.ServerUnavailableException
import com.aura.ui.states.State
import com.aura.ui.transfer.TransferEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val dataRepository: AuraRepositoryInterface) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState(State.Idle))
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    private val _eventsFlow = Channel<TransferEvent>()
    val eventsFlow = _eventsFlow.receiveAsFlow()

    private fun sendToast(@StringRes msg: Int) {
        _eventsFlow.trySend(TransferEvent.ShowToast(msg))
    }

    fun onLoginFieldsChanged(id: String, password: String) {
        _uiState.update { it.copy(isLoginEnabled = id.isNotBlank() && password.isNotBlank()) }
    }

    fun loginData(id: String, password: String) {
        _uiState.update { it.copy(result = State.Loading) }

        dataRepository.fetchLoginData(id, password)

            .onEach { isGranted ->
                _eventsFlow.trySend(
                    TransferEvent.ShowToast(
                        if (isGranted) R.string.login_success else R.string.login_fail
                    )
                )
                _uiState.update {
                    it.copy(
                        result = if (isGranted) State.Success else State.Error.LoginError
                    )
                }
            }
            .catch { error ->
                val message = when (error) {
                    is NoConnectionException -> R.string.no_internet
                    is ServerUnavailableException -> R.string.error_server
                    else -> R.string.unknown_error
                }

                _uiState.update { it.copy(result = State.Idle) }
                sendToast(message)
            }
            .launchIn(viewModelScope)
    }
}

data class LoginUIState(
    val result: State = State.Idle,
    val isLoginEnabled: Boolean = false
)