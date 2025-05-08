package com.aura.repository

import com.aura.ui.data.network.AuraClient
import com.aura.ui.domain.model.LoginModel
import com.aura.ui.domain.model.TransferModel
import com.aura.ui.home.HomeResponse
import com.aura.ui.login.LoginResponse
import com.aura.ui.transfer.TransferResponse
import retrofit2.Response

open class FakeAuraClient : AuraClient {
    override suspend fun userId(id: String): List<HomeResponse> {
        return if (id == "1234") {
            listOf(
                HomeResponse("1", true, 523.23),
                HomeResponse("2", false, 235.22)
            )
        } else {
            emptyList()
        }
    }

    override suspend fun loginDetails(request: LoginModel): LoginResponse {
        return LoginResponse(granted = (request.id == "1234" && request.password == "p@sswOrd"))
    }

    override suspend fun transferDetails(request: TransferModel): Response<TransferResponse> {
        return Response.success(TransferResponse(true))
    }

}
