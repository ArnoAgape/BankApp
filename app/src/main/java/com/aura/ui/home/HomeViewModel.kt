package com.aura.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.network.repository.AuraRepository
import com.aura.ui.domain.model.UserModel
import com.aura.ui.states.State
import com.aura.ui.states.errors.NoConnectionException
import com.aura.ui.states.errors.ServerUnavailableException
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
class HomeViewModel @Inject constructor(private val dataRepository: AuraRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

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
                Log.e(
                    "HomeViewModel",
                    "Erreur pendant la récupération des données : ${error.message}"
                )
                if (error is NoConnectionException) {
                    _uiState.update {
                        it.copy(result = State.Error.NoInternet)
                    }
                }
                if (error is ServerUnavailableException) {
                    _uiState.update {
                        it.copy(result = State.Error.Server)
                    }
                } else _uiState.update {
                    it.copy(result = State.Error.UnknownError)
                }
            }
            .launchIn(viewModelScope)
    }
}

data class HomeUIState(
    val result: State = State.Idle,
    val balance: List<UserModel> = emptyList()
)
