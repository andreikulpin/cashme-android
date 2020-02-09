package co.cashme.cashme.di.modules

import co.cashme.cashme.data.network.api.ExchangePointsApi
import co.cashme.cashme.di.provider.ExchangePointsApiProvider
import toothpick.config.Module

class NetworkModule : Module() {
    init {
        bind(ExchangePointsApi::class.java).toProvider(ExchangePointsApiProvider::class.java)
            .providesSingleton()
    }
}