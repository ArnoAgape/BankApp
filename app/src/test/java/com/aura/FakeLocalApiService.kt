package com.aura

import com.aura.ui.domain.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalApiService {

    private val users = listOf(
        User("1234", "Pierre", "Brisette", "p@sswOrd",
            listOf(
                Account("1", true, 2354.23),
                Account("2", false, 235.22),
            )
        ),
        User("5678", "Gustave", "Charbonneau", "T0pSecr3t",
            listOf(
                Account("3", false, 24.53),
                Account("4", true, 10032.21),
            )
        )
    )

    fun login(identifier: String, password: String): Flow<Boolean> = flow {
        val isValid = users.any { it.id == identifier && it.password == password }
        emit(isValid)
    }

    fun user(id: String): Flow<List<UserModel>> = flow {
        val user = users.find { it.id == id }
        val userModels = user?.accounts?.map { account ->
            UserModel(
                id = account.id,
                main = account.main,
                balance = account.balance
            )
        } ?: emptyList()

        emit(userModels)
    }

    fun transfer(sender: String, recipient: String, amount: Double): Flow<Boolean> = flow {
        val senderUser = users.find { it.id == sender }
        val recipientExists = users.any { it.id == recipient}
        val mainAccountBalance = senderUser?.accounts?.find { it.main }!!.balance
        val isValid = recipientExists && mainAccountBalance >= amount
        emit(isValid)
    }
}

data class User(
    val id: String,
    val firstname: String,
    val lastname: String,
    val password: String,
    val accounts: List<Account>,
)

data class Account(
    val id: String,
    val main: Boolean,
    var balance: Double
)