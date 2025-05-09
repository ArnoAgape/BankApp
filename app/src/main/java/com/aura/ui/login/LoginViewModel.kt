package com.aura.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.R
import com.aura.ui.data.network.repository.AuraRepositoryInterface
import com.aura.ui.states.State
import com.aura.ui.states.errors.NoConnectionException
import com.aura.ui.states.errors.ServerUnavailableException
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

/**
 * `LoginViewModel` manages the state and business logic of the login screen.
 *
 * It interacts with the repository to perform the login request and exposes two flows:
 * - `uiState`: holds the login form state, including loading status and button enabled state
 * - `eventsFlow`: emits one-time UI events such as toasts
 *
 * This ViewModel handles three core responsibilities:
 * 1. Validating login form input
 * 2. Performing the login API call
 * 3. Updating the UI state or sending events based on the result
 *
 * @property dataRepository The repository interface used to interact with the login API
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val dataRepository: AuraRepositoryInterface) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState(State.Idle))
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    private val _eventsFlow = Channel<LoginEvent>()
    val eventsFlow = _eventsFlow.receiveAsFlow()

    /**
     * Sends a one-time toast event to the UI using a string resource.
     *
     * @param msg The string resource ID to be shown in the toast.
     */
    private fun sendToast(@StringRes msg: Int) {
        _eventsFlow.trySend(LoginEvent.ShowToast(msg))
    }

    /**
     * Called every time the login or password fields are updated.
     * Enables or disables the login button based on input validity.
     *
     * @param id The user ID.
     * @param password The password.
     */
    fun onLoginFieldsChanged(id: String, password: String) {
        _uiState.update { it.copy(isLoginEnabled = id.isNotBlank() && password.isNotBlank()) }
    }

    /**
     * Attempts to log in the user using the provided credentials.
     * Updates the UI state to loading, and handles the result via success or error.
     *
     * On success or failure, emits a toast event and updates the login state accordingly.
     *
     * @param id The user ID.
     * @param password The password.
     */
    fun loginData(id: String, password: String) {
        _uiState.update { it.copy(result = State.Loading) }

        dataRepository.fetchLoginData(id, password)

            .onEach { isGranted ->
                _eventsFlow.trySend(
                    LoginEvent.ShowToast(
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

/**
 * Represents the UI state for the login screen.
 *
 * @param result The current login state (Idle, Loading, Success, or Error)
 * @param isLoginEnabled Indicates whether the login button should be enabled
 */
data class LoginUIState(
    val result: State = State.Idle,
    val isLoginEnabled: Boolean = false
)