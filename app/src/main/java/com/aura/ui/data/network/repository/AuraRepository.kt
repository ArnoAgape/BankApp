package com.aura.ui.data.network.repository

import android.content.Context
import android.util.Log
import com.aura.ui.data.network.AuraClient
import com.aura.ui.states.errors.NetworkStatusChecker
import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.UserModel
import com.aura.ui.states.errors.NoConnectionException
import com.aura.ui.states.errors.ServerUnavailableException
import com.aura.ui.states.errors.UnknownUserException
import com.aura.ui.domain.model.TransferModel
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
                    throw ServerUnavailableException() // Erreur serveur
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

    fun fetchTransferData(sender: String, recipient: String, amount: Double): Flow<Boolean> = flow {
        Log.d("fetchTransferDataRepository", "Envoyé: sender=$sender, recipient=$recipient, amount=$amount")

        val response = dataService.transferDetails(TransferModel(sender, recipient, amount))

        if (response.isSuccessful) {
            val body = response.body() ?: throw Exception()
            emit(body.result)
        } else {
            val code = response.code()

            when (code) {
                500 -> throw UnknownUserException()
                503 -> throw ServerUnavailableException()
                else -> throw Exception()
            }
        }

    }.catch { error ->
        Log.e("AuraRepository", error.message ?: "")
        when (error) {
            is IOException -> {
                if (!networkChecker.hasInternetConnection()) {
                    throw NoConnectionException()
                } else {
                    throw ServerUnavailableException()
                }
            }
            else -> throw error
        }
    }
}