package com.aura.ui.home

import androidx.annotation.StringRes

sealed interface HomeEvent {
    data class ShowToast(@StringRes val message: Int) : HomeEvent
}