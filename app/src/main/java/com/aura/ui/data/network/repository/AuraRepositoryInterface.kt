package com.aura.ui.data.network.repository

import com.aura.ui.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface AuraRepositoryInterface {
    fun fetchLoginData(id: String, password: String): Flow<Boolean>
    fun fetchUserData(id: String): Flow<List<UserModel>>
    fun fetchTransferData(sender: String, recipient: String, amount: Double): Flow<Boolean>
}