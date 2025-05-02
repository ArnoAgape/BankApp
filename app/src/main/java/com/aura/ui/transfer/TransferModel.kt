package com.aura.ui.transfer

data class TransferModel(
    val senderId: String,
    val recipientId: String,
    val amount: Double
)
