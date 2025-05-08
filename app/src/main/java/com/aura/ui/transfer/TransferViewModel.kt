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

@HiltViewModel
class TransferViewModel @Inject constructor(private val dataRepository: AuraRepositoryInterface) :
    ViewModel() {

    private val _uiState = MutableStateFlow(TransferUIState(State.Idle))
    val uiState: StateFlow<TransferUIState> = _uiState.asStateFlow()

    private val _eventsFlow = Channel<TransferEvent>()
    val eventsFlow = _eventsFlow.receiveAsFlow()

    private fun sendToast(@StringRes msg: Int) {
        _eventsFlow.trySend(TransferEvent.ShowToast(msg))
    }

    fun onLoginFieldsChanged(recipient: String, amount: String) {
        _uiState.update { it.copy(
            isTransferEnabled = recipient.isNotBlank() && amount.isNotBlank())
        }
    }

    fun transferData(sender: String, recipient: String, amount: String) {
        if (amount < "1") {
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

data class TransferUIState(
    val result: State = State.Idle,
    val isTransferEnabled: Boolean = false
)