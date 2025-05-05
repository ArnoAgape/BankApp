package com.aura.ui.home

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeResponse(
    @Json(name = "id")
    val id: String,
    @Json(name = "main")
    val main: Boolean,
    @Json(name = "balance")
    val balance: Double
)