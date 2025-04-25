package com.aura.ui.data.network.repository

import android.util.Log
import com.aura.ui.data.network.LoginClient
import com.aura.ui.domain.model.LoginModel
import com.aura.ui.login.NoConnectionException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okio.IOException

class LoginRepository(private val loginClient: LoginClient) {

    fun fetchLoginData(id: String, password: String): Flow<Boolean> = flow {
        val request = loginClient.loginDetails(LoginModel(id, password))
        Log.d("LoginRepository", "Envoyé: id=$id, password=$password")
        emit(request.granted)
    }.catch { error ->
        if (error is IOException) {
            throw NoConnectionException()
        }
        else emit(false)
        Log.e("LoginRepository", error.message ?: "")
    }
}