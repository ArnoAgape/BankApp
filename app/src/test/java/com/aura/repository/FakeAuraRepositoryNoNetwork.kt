package com.aura.repository

import com.aura.ui.data.network.repository.AuraRepositoryInterface
import com.aura.ui.domain.model.TransferModel
import com.aura.ui.domain.model.UserModel
import com.aura.ui.states.errors.ServerUnavailableException
import com.aura.ui.states.errors.UnknownUserException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeAuraRepositoryNoNetwork(private val dataService: FakeAuraClient) : AuraRepositoryInterface {
    override fun fetchTransferData(sender: String, recipient: String, amount: Double): Flow<Boolean> = flow {
        val response = dataService.transferDetails(TransferModel(sender, recipient, amount))
        if (response.isSuccessful) {
            val body = response.body() ?: throw Exception()
            emit(body.result)
        } else {
            when (response.code()) {
                500 -> throw UnknownUserException()
                503 -> throw ServerUnavailableException()
                else -> throw Exception()
            }
        }
    }

    override fun fetchLoginData(id: String, password: String) = flowOf(true)
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
    }
}
