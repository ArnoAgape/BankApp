package com.aura.repository

import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName

class FakeLocalApiService {

    private val users = listOf(
        User("1234", "Pierre", "Brisette", "p@sswOrd",
            listOf(
                Account("1", true, 523.23),
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

}

data class User(
    val id: String,
    val firstname: String,
    val lastname: String,
    val password: String,
    val accounts: List<Account>,
)

data class Account(
    @SerialName("id") val id: String,
    @SerialName("main") val main: Boolean,
    @SerialName("balance") var balance: Double
)