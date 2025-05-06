package com.aura.ui.login

import androidx.annotation.StringRes

sealed interface LoginEvent {
    data class ShowToast(@StringRes val message: Int) : LoginEvent
}