package co.cashme.cashme.presentation.feature.map.mapper

import co.cashme.cashme.R
import co.cashme.cashme.domain.model.Currency
import co.cashme.cashme.domain.model.ExchangeAction
import co.cashme.cashme.domain.model.ExchangePoint
import co.cashme.cashme.presentation.custom.mapradiogroup.MapRadioButtonModel
import co.cashme.cashme.presentation.feature.map.model.PointInfoModel
import co.cashme.cashme.utils.ResourceDelegate
import javax.inject.Inject

class MapViewModelMapper @Inject constructor(
    private val resources: ResourceDelegate
) {
    private val sellString = resources.getString(R.string.exchange_action_sell)
    private val buyString = resources.getString(R.string.exchange_action_buy)

    fun mapExchangeActions(selectedAction: ExchangeAction): List<MapRadioButtonModel> =
        ExchangeAction.values().map {
            val title = when (it) {
                ExchangeAction.SELL -> sellString
                ExchangeAction.BUY -> buyString
            }

            MapRadioButtonModel(
                id = it.ordinal,
                title = title,
                checked = it == selectedAction
            )
        }

    fun mapCurrencies(selectedCurrency: Currency): List<MapRadioButtonModel> =
        Currency.values().map {
            MapRadioButtonModel(
                id = it.ordinal,
                title = it.name,
                checked = it == selectedCurrency
            )
        }

    fun mapPointInfo(point: ExchangePoint): PointInfoModel {
        val rates = point.rates.map {
            "${it.currency.name} $sellString: ${it.sellCost.value} $buyString: ${it.buyCost.value}"
        }

        return PointInfoModel(
            title =  point.name,
            rates = rates,
            phone = resources.getString(R.string.phone, point.phone),
            address = resources.getString(R.string.address, point.address)
        )
    }
}