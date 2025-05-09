package com.aura.ui.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.R
import com.aura.ui.data.network.repository.AuraRepositoryInterface
import com.aura.ui.domain.model.UserModel
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
 * `HomeViewModel` manages the UI state of the `HomeActivity`.
 *
 * It is responsible for fetching the user's account data (e.g., balances)
 * from the repository using their user ID. The result is exposed through a `StateFlow`
 * to update the UI reactively, and one-time events like toasts are sent through a `Channel`.
 *
 * @param dataRepository The repository interface used to fetch user account data.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val dataRepository: AuraRepositoryInterface) :
    ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    private val _eventsFlow = Channel<HomeEvent>()
    val eventsFlow = _eventsFlow.receiveAsFlow()

    /**
     * Sends a one-time toast event to the UI using a string resource.
     *
     * @param msg The string resource ID to be shown in the toast.
     */
    private fun sendToast(@StringRes msg: Int) {
        _eventsFlow.trySend(HomeEvent.ShowToast(msg))
    }

    /**
     * Fetches user account data based on the given user ID.
     *
     * This function:
     * - Updates the UI state to `Loading`
     * - Calls the repository to fetch a list of user accounts
     * - Updates the state with the retrieved data and sets `Success`
     * - Handles and maps errors (e.g., no internet, server error) to appropriate toast messages
     *
     * @param id The ID of the user whose account data should be retrieved.
     */
    fun getUserId(id: String) {
        _uiState.update {
            it.copy(result = State.Loading)
        }

        dataRepository.fetchUserData(id)

            .onEach { state ->
                _uiState.update {
                    it.copy(
                        balance = state,
                        result = State.Success
                    )
                }
            }
            .catch { error ->
                val message =
                    when (error) {
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
 * @param result The current home state (Idle, Loading, Success, or Error)
 * @param balance The current balance to display
 */
data class HomeUIState(
    val result: State = State.Idle,
    val balance: List<UserModel> = emptyList()
)
