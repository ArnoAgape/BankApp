package com.aura.ui.data.network

import com.aura.ui.domain.model.LoginModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuraClient {
    @POST("/login")
    suspend fun loginDetails(@Body request: LoginModel): LoginResponse

    @GET("/accounts/{id}")
    suspend fun userId(
        @Query(value = "id") id: String
    ): List<Account>
}