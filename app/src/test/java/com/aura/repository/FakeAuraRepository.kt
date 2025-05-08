package com.aura.repository

import com.aura.ui.data.network.repository.AuraRepositoryInterface
import com.aura.ui.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

class FakeAuraRepository(
    private val api: FakeLocalApiService
) : AuraRepositoryInterface {

    override fun fetchLoginData(id: String, password: String):
            Flow<Boolean> = api.login(id, password)
    override fun fetchUserData(id: String): Flow<List<UserModel>> = api.user(id)
    override fun fetchTransferData(sender: String, recipient: String, amount: Double):
            Flow<Boolean> = api.transfer(sender, recipient, amount)
}
