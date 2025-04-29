package com.aura.ui.data.network.repository

import android.util.Log
import com.aura.ui.data.network.AuraClient
import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.UserModel
import com.aura.ui.login.NoConnectionException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okio.IOException

class AuraRepository(private val dataService: AuraClient) {

    fun fetchLoginData(id: String, password: String): Flow<Boolean> = flow {
        val request = dataService.loginDetails(LoginModel(id, password))
        Log.d("fetchLoginData", "Envoyé: id=$id, password=$password")
        emit(request.granted)
    }.catch { error ->
        if (error is IOException) {
            throw NoConnectionException()
        } else emit(false)
        Log.e("LoginRepository", error.message ?: "")
    }

    fun fetchUserData(id: String): Flow<List<UserModel>> = flow {
        val accounts = dataService.userId(id)
        Log.d("fetchUserData", "Reçu: id=$id")
        val model = accounts.map { account ->
            UserModel(
                id = account.id,
                main = account.main,
                balance = account.balance
            )
        }
        emit(model)
    }.catch { error ->
        Log.e("WeatherRepository", error.message ?: "")
    }
}