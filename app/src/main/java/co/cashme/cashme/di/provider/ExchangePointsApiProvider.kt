package co.cashme.cashme.di.provider

import co.cashme.cashme.BuildConfig
import co.cashme.cashme.data.network.api.ExchangePointsApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Provider

class ExchangePointsApiProvider @Inject constructor() : Provider<ExchangePointsApi> {
    override fun get(): ExchangePointsApi = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_API_URL)
        .build()
        .create(ExchangePointsApi::class.java)
}