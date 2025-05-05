package com.aura.repository

import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.UserModel

class FakeLocalApiService {

    private val loginId = listOf(
        LoginModel("1234", "p@sswOrd"),
        LoginModel("5678", "T0pSecr3t")
    )

    private val userId = listOf(
        UserModel("1", true, 523.23),
        UserModel("2", false, 235.22),
        UserModel("3", false, 24.53),
        UserModel("4", true, 10032.21)
    )

}