package com.aura.ui.transfer

import androidx.annotation.StringRes

sealed interface LoginEvent {
    data class ShowToast(@StringRes val message: Int) : LoginEvent
}