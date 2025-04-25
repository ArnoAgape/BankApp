package com.aura.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.network.repository.AuraRepository
import com.aura.ui.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val dataRepository: AuraRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(HomeUISTate())
    val uiState: StateFlow<HomeUISTate> = _uiState.asStateFlow()

    fun getUserData(id: String, password: String) {
        dataRepository.fetchUserData(id, password)
            .onEach { state ->
                _uiState.update { currentState ->
                    currentState.copy(
                        account = state
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}

data class HomeUISTate(
    val account:List<UserModel> = emptyList()
)
