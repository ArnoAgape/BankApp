package com.aura.ui.data.network.repository

import android.content.Context
import android.util.Log
import com.aura.ui.data.network.AuraClient
import com.aura.ui.di.errors.NetworkStatusChecker
import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.UserModel
import com.aura.ui.di.errors.NoConnectionException
import com.aura.ui.di.errors.ServerUnavailableException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuraRepository @Inject constructor(@ApplicationContext context: Context, private val dataService: AuraClient) {

    private val networkChecker = NetworkStatusChecker(context)

    fun fetchLoginData(id: String, password: String): Flow<Boolean> = flow {
        val request = dataService.loginDetails(LoginModel(id, password))
        Log.d("fetchLoginData", "Envoyé: id=$id, password=$password")
        emit(request.granted)
    }.catch { error ->
        when (error) {
            is IOException -> {
                if (!networkChecker.hasInternetConnection()) {
                    throw NoConnectionException() // Pas d'internet
                } else {
                    throw ServerUnavailableException()
                }
            }

            else -> throw error // Erreur inconnue
        }
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
        when (error) {
            is IOException -> {
                if (!networkChecker.hasInternetConnection()) {
                throw NoConnectionException() // Pas d'internet
            } else {
                throw ServerUnavailableException()
                }
            }

            else -> throw error // Erreur inconnue
        }
    }
}