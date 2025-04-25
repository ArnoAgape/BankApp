package com.aura.ui.data.network

import com.aura.ui.domain.model.LoginModel
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginClient {
    @POST("/login")
    suspend fun loginDetails(@Body request: LoginModel): CredentialsResult
}