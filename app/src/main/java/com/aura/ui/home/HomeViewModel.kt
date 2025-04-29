package com.aura.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.network.repository.AuraRepository
import com.aura.ui.domain.model.UserModel
import com.aura.ui.login.LoginState
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
        dataRepository.fetchUserData(id)
            .catch { error ->
                Log.e("HomeViewModel", "Erreur pendant la récupération des données : ${error.message}")
            }
            .onEach { state ->
                _uiState.update { currentState ->
                    currentState.copy(balance = state)
                }
            }
            .launchIn(viewModelScope)
    }
}

data class HomeUIState(
    val result: LoginState = LoginState.Idle,
    val balance:List<UserModel> = emptyList()
)
