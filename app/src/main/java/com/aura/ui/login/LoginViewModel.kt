package com.aura.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.network.repository.LoginRepository
import com.aura.ui.domain.model.ResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.Response
import java.nio.channels.NoConnectionPendingException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val dataRepository: LoginRepository) : ViewModel() {
    private val _isLoginEnabled = MutableStateFlow(false)
    val isLoginEnabled: StateFlow<Boolean> = _isLoginEnabled
    private val _uiState = MutableStateFlow(LoginUIState(LoginState.Idle))
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun onLoginFieldsChanged(id: String, password: String) {
        _isLoginEnabled.value = id.isNotBlank() && password.isNotBlank()
    }

    fun loginData(id: String, password: String) {
        viewModelScope.launch {
            dataRepository.fetchLoginData(id, password)
                .onEach { isGranted ->
                    _uiState.update {
                        it.copy(
                            result = if (isGranted) LoginState.Success else LoginState.Error
                        )
                    }
                }
                .catch { error ->
                    if (error is NoConnectionException) {
                        _uiState.update { it.copy(result = LoginState.NoInternet) }
                    }
                }
                .launchIn(viewModelScope)
        }
    }
}

data class LoginUIState(
    var result: LoginState = LoginState.Idle
)