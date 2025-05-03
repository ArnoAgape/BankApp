package com.aura.ui.domain.model

data class TransferModel(
    val sender: String,
    val recipient: String,
    val amount: Double
)