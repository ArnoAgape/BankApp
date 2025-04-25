package com.aura.ui.data.network

import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.UserModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Calendar

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "list")
    val accounts: List<Account>
) {

    @JsonClass(generateAdapter = true)
    data class Account(
        @Json(name = "id")
        val id: String,
        @Json(name = "main")
        val main: Boolean,
        @Json(name = "balance")
        val balance: Double
    )

    fun toDomainModel(): List<UserModel> {
        return accounts.map { account ->
            val accountId = account.id
            val isMainAccount = account.main
            val balanceAmount = account.balance
            UserModel(
                id = accountId,
                main = isMainAccount,
                balance = balanceAmount
            )
        }
    }
}