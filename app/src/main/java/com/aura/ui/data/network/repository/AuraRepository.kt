package com.aura.ui.data.network.repository

import android.content.Context
import com.aura.ui.data.network.AuraClient
import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.TransferModel
import com.aura.ui.domain.model.UserModel
import com.aura.ui.states.errors.NetworkStatusChecker
import com.aura.ui.states.errors.NoConnectionException
import com.aura.ui.states.errors.ServerUnavailableException
import com.aura.ui.states.errors.UnknownUserException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * `AuraRepository` is the main implementation of `AuraRepositoryInterface`.
 *
 * It handles all data interactions between the app and the remote API via `AuraClient`.
 * It also manages error mapping for internet and server-related issues using `NetworkStatusChecker`.
 *
 * This class provides three main functions:
 * - `fetchLoginData`: checks login credentials
 * - `fetchUserData`: retrieves user account details
 * - `fetchTransferData`: performs a money transfer between users
 *
 * @constructor Injected via Hilt with application context and an instance of `AuraClient`
 * @param context Application context used for network checking
 * @param dataService Retrofit client for API communication
 */
@Singleton
class AuraRepository @Inject constructor(private val networkStatusChecker: NetworkStatusChecker, private val dataService: AuraClient) : AuraRepositoryInterface {

    /**
     * Attempts to log in the user with the provided ID and password.
     *
     * Calls the API endpoint and emits `true` if the login is granted.
     * Catches IO/network exceptions and throws domain-specific errors like:
     * - `NoConnectionException` if there's no internet
     * - `ServerUnavailableException` if the server is unreachable
     *
     * @param id The user's login identifier.
     * @param password The user's password.
     * @return A flow emitting a Boolean representing the login success.
     */
    override fun fetchLoginData(id: String, password: String): Flow<Boolean> = flow {
        val request = dataService.loginDetails(LoginModel(id, password))
        emit(request.granted)
    }.catch { error ->
        when (error) {
            is IOException -> {
                if (!networkStatusChecker.hasInternetConnection()) {
                    throw NoConnectionException() // No Internet
                } else {
                    throw ServerUnavailableException() // Server error
                }
            }

            else -> throw error // Unknown error
        }
    }

    /**
     * Retrieves the list of user accounts associated with the given user ID.
     *
     * Transforms the API response into a list of `UserModel`.
     * Handles and maps common connectivity issues to custom exceptions.
     *
     * @param id The user's identifier.
     * @return A flow emitting a list of user account models.
     */
    override fun fetchUserData(id: String): Flow<List<UserModel>> = flow {
        val accounts = dataService.userId(id)
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
                if (!networkStatusChecker.hasInternetConnection()) {
                throw NoConnectionException() // No Internet
            } else {
                throw ServerUnavailableException()
                }
            }

            else -> throw error // Unknown error
        }
    }

    /**
     * Performs a transfer from the sender to the recipient for the given amount.
     *
     * Emits `true` if the transfer was successful, or throws appropriate exceptions
     * based on the HTTP status code or connectivity issues:
     * - `UnknownUserException` for 500 server errors
     * - `ServerUnavailableException` for 503 or connection problems
     * - `NoConnectionException` if there is no internet connection
     *
     * @param sender The ID of the sender.
     * @param recipient The ID of the recipient.
     * @param amount The amount to transfer.
     * @return A flow emitting a Boolean indicating whether the transfer was successful.
     */
    override fun fetchTransferData(sender: String, recipient: String, amount: Double): Flow<Boolean> = flow {

        val response = dataService.transferDetails(TransferModel(sender, recipient, amount))
        emit (response.isSuccessful)

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
        when (error) {
            is IOException -> {
                if (!networkStatusChecker.hasInternetConnection()) {
                    throw NoConnectionException()
                } else {
                    throw ServerUnavailableException()
                }
            }
            else -> throw error
        }
    }
}