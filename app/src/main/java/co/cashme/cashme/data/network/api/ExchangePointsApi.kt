package co.cashme.cashme.data.network.api

import co.cashme.cashme.data.network.model.ExchangePointApiModel
import co.cashme.cashme.domain.model.ExchangePoint
import retrofit2.http.GET

interface ExchangePointsApi {
    @GET("/load")
    suspend fun fetchPoints(): List<ExchangePointApiModel>
}