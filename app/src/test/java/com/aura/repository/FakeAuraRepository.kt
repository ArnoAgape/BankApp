package com.aura.repository

import com.aura.ui.data.network.repository.AuraRepositoryInterface
import com.aura.ui.domain.model.UserModel
import com.aura.ui.states.errors.NoConnectionException
import com.aura.ui.states.errors.ServerUnavailableException
import com.aura.ui.states.errors.UnknownUserException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuraRepository : AuraRepositoryInterface {

    private var shouldReturnError = false
    private var loginGranted = true
    private var transferSuccess = true

    fun setLoginGranted(granted: Boolean) {
        loginGranted = granted
    }

    fun setTransferSuccess(success: Boolean) {
        transferSuccess = success
    }

    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override fun fetchLoginData(id: String, password: String): Flow<Boolean> = flow {
        if (shouldReturnError) throw NoConnectionException()
        emit(loginGranted)
    }

    override fun fetchUserData(id: String): Flow<List<UserModel>> = flow {
        if (shouldReturnError) throw ServerUnavailableException()
        emit(
            listOf(
                UserModel("1", true, 523.23),
                UserModel("2", false, 235.22),
                UserModel("3", false, 24.53),
                UserModel("4", true, 10032.21)
            )
        )
    }

    override fun fetchTransferData(sender: String, recipient: String, amount: Double): Flow<Boolean> =
        flow {
            if (shouldReturnError) throw UnknownUserException()
            emit(transferSuccess)
        }
}