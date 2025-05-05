package com.aura.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.network.repository.AuraRepository
import com.aura.ui.states.errors.NoConnectionException
import com.aura.ui.states.errors.ServerUnavailableException
import com.aura.ui.states.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val dataRepository: AuraRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState(State.Idle))
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun onLoginFieldsChanged(id: String, password: String) {
        _uiState.update {
            it.copy(
                isLoginEnabled = id.isNotBlank() && password.isNotBlank()
            )
        }
    }

    fun loginData(id: String, password: String) {
        _uiState.update {
            it.copy(result = State.Loading)
        }
        dataRepository.fetchLoginData(id, password)
            .onEach { isGranted ->
                _uiState.update {
                    it.copy(
                        result = if (isGranted) State.Success else State.Error.LoginError
                    )
                }
            }
            .catch { error ->
                if (error is NoConnectionException) {
                    _uiState.update { it.copy(result = State.Error.NoInternet) }
                }
                if (error is ServerUnavailableException) {
                    _uiState.update { it.copy(result = State.Error.Server) }
                }
            }
            .launchIn(viewModelScope)
    }
}

data class LoginUIState(
    val result: State = State.Idle,
    val isLoginEnabled: Boolean = false
)