package com.aura.ui.transfer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.network.repository.AuraRepository
import com.aura.ui.di.errors.NoConnectionException
import com.aura.ui.di.errors.ServerUnavailableException
import com.aura.ui.di.errors.UnknownUserException
import com.aura.ui.login.State
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
class TransferViewModel @Inject constructor(private val dataRepository: AuraRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(TransferUIState(State.Idle))
    val uiState: StateFlow<TransferUIState> = _uiState.asStateFlow()

    fun onLoginFieldsChanged(recipient: String, amount: String) {
        _uiState.update {
            it.copy(
                isTransferEnabled = recipient.isNotBlank() && amount.isNotBlank()
            )
        }
    }

    fun ifSameId(sender: String, recipient: String) {
        _uiState.update {
            it.copy(
                sameUserId = sender.toString() !== recipient.toString()
            )
        }
    }

    fun transferData(sender: String, recipient: String, amount: String, senderBalance: String) {
        _uiState.update {
            it.copy(result = State.Loading)
        }
        Log.d("fetchTransferData", "Envoyé: sender=$sender, recipient=$recipient, amount=$amount, balance=$senderBalance")
        dataRepository.fetchTransferData(sender, recipient, amount.toDouble())
            .onEach { result ->
                _uiState.update {
                    it.copy(
                        result = if (result) State.Success else State.Error.InsufficientBalance
                    )
                }
            }.catch { error ->
                when (error) {
                    is NoConnectionException -> {
                        _uiState.update { it.copy(result = State.Error.NoInternet) }
                    }
                    is UnknownUserException -> {
                        _uiState.update { it.copy(result = State.Error.UnknownId) }
                    }
                    is ServerUnavailableException -> {
                        _uiState.update { it.copy(result = State.Error.Server) }
                    }
                    else -> {
                        _uiState.update { it.copy(result = State.Error.UnknownError) } // facultatif
                        Log.e("TransferViewModel", "Erreur inconnue : ${error.message}")
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}

data class TransferUIState(
    val result: State = State.Idle,
    val isTransferEnabled: Boolean = false,
    val sameUserId: Boolean = false
)