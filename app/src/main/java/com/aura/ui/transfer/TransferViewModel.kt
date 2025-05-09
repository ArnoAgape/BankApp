package com.aura.ui.transfer

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.R
import com.aura.ui.data.network.repository.AuraRepositoryInterface
import com.aura.ui.states.State
import com.aura.ui.states.errors.NoConnectionException
import com.aura.ui.states.errors.ServerUnavailableException
import com.aura.ui.states.errors.UnknownUserException
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
 * `TransferViewModel` handles the business logic for performing money transfers.
 *
 * It receives the sender's ID, recipient's ID, and amount to send, then delegates the transfer
 * operation to the repository. It exposes a `uiState` for reactive UI updates and uses a `Channel`
 * to send one-time events such as toasts.
 *
 * @param dataRepository The repository interface used to perform transfer operations.
 */
@HiltViewModel
class TransferViewModel @Inject constructor(private val dataRepository: AuraRepositoryInterface) :
    ViewModel() {

    private val _uiState = MutableStateFlow(TransferUIState(State.Idle))
    val uiState: StateFlow<TransferUIState> = _uiState.asStateFlow()

    private val _eventsFlow = Channel<TransferEvent>()
    val eventsFlow = _eventsFlow.receiveAsFlow()

    /**
     * Sends a one-time toast event to the UI using a string resource.
     *
     * @param msg The string resource ID to be shown in the toast.
     */
    private fun sendToast(@StringRes msg: Int) {
        _eventsFlow.trySend(TransferEvent.ShowToast(msg))
    }

    /**
     * Called every time the login or password fields are updated.
     * Enables or disables the login button based on input validity.
     *
     * @param recipient The recipient ID.
     * @param amount The amount.
     */
    fun onLoginFieldsChanged(recipient: String, amount: String) {
        _uiState.update { it.copy(
            isTransferEnabled = recipient.isNotBlank() && amount.isNotBlank())
        }
    }

    /**
     * Initiates a money transfer from the sender to the recipient.
     *
     * This function:
     * - Validates the amount is not zero
     * - Updates the UI state to `Loading`
     * - Calls the repository to perform the transfer
     * - Updates the state to `Success` or `Error.InsufficientBalance` based on the result
     * - Sends a toast message corresponding to the result or error
     *
     * Handles common errors such as:
     * - No internet connection
     * - Unknown recipient
     * - Server unavailability
     *
     * @param sender The ID of the sender account.
     * @param recipient The ID of the recipient account.
     * @param amount The transfer amount as a string (converted to `Double`).
     */
    fun transferData(sender: String, recipient: String, amount: String) {
        if (amount == "0") {
            sendToast(R.string.empty_amount)
            _uiState.update { it.copy(result = State.Idle) }
            return
        }

        _uiState.update { it.copy(result = State.Loading) }

        dataRepository.fetchTransferData(sender, recipient, amount.toDouble())

            .onEach { result ->
                _eventsFlow.trySend(
                    TransferEvent.ShowToast(
                        if (result) R.string.transfer_success else R.string.transfer_failed
                    )
                )
                _uiState.update { it.copy(
                    result = if (result) State.Success else State.Error.InsufficientBalance) }

            }.catch { error ->
                val message = when (error) {
                    is NoConnectionException -> R.string.no_internet
                    is UnknownUserException -> R.string.unknown_user
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
 * Represents the UI state for the transfer screen.
 *
 * @param result The current home state (Idle, Loading, Success, or Error)
 * @param isTransferEnabled Indicates whether the transfer button should be enabled
 */
data class TransferUIState(
    val result: State = State.Idle,
    val isTransferEnabled: Boolean = false
)