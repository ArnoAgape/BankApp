package com.aura.ui.transfer

import androidx.annotation.StringRes

sealed interface TransferEvent {
    data class ShowToast(@StringRes val message: Int) : TransferEvent
}