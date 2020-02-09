package co.cashme.cashme.data.repository

import co.cashme.cashme.data.network.api.ExchangePointsApi
import co.cashme.cashme.data.network.mapper.PlaceApiModelMapper
import co.cashme.cashme.domain.model.ExchangePoint
import co.cashme.cashme.domain.model.Result
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class ExchangePointsRepository @Inject constructor(
    private val exchangePointsApi: ExchangePointsApi,
    private val mapper: PlaceApiModelMapper
) {
    suspend fun getExchangePoints(): Result<List<ExchangePoint>> = try {
        exchangePointsApi.fetchPoints()
            .let(mapper::mapExchangePoints)
            .let { Result.Success(it) }
    } catch (e: Exception) {
        Timber.e(e)
        Result.Error(e)
    }
}