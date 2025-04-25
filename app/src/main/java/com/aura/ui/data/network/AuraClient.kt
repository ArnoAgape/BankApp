package com.aura.ui.data.network

import com.aura.ui.domain.model.LoginModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuraClient {
    @POST("/login")
    suspend fun loginDetails(@Body request: LoginModel): LoginResponse

    @GET("/accounts/")
    suspend fun userDetails(
        @Query(value = "id") id: String,
        @Query(value = "password") password: String,
    ): Response<UserResponse>
}