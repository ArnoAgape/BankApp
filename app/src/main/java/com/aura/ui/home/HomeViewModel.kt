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

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataRepository: AuraRepositoryInterface) :
    ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    private val _eventsFlow = Channel<HomeEvent>()
    val eventsFlow = _eventsFlow.receiveAsFlow()

    private fun sendToast(@StringRes msg: Int) {
        _eventsFlow.trySend(HomeEvent.ShowToast(msg))
    }

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

data class HomeUIState(
    val result: State = State.Idle,
    val balance: List<UserModel> = emptyList()
)
