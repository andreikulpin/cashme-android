package co.cashme.cashme.data.network.mapper

import co.cashme.cashme.data.network.model.ExchangePointApiModel
import co.cashme.cashme.domain.model.Currency
import co.cashme.cashme.domain.model.ExchangePoint
import co.cashme.cashme.domain.model.Rate
import javax.inject.Inject

class PlaceApiModelMapper @Inject constructor() {
    fun mapExchangePoints(apiModels: List<ExchangePointApiModel>): List<ExchangePoint> {
        val rateRanges = Currency.values().map { it.ordinal + 2 }.map { currency ->
            val minSell = apiModels.asSequence().filter { it.currency == currency }.map { it.sell }.min() ?: 0f
            val maxSell = apiModels.asSequence().filter { it.currency == currency }.map { it.sell }.max() ?: 0f
            val minBuy = apiModels.asSequence().filter { it.currency == currency }.map { it.buy }.min() ?: 0f
            val maxBuy = apiModels.asSequence().filter { it.currency == currency }.map { it.buy }.max() ?: 0f

            currency to RateRange(
                sell = minSell .. maxSell,
                buy = minBuy .. maxBuy
            )
        }
            .toMap()

        return apiModels.groupBy { it.name }
            .map { (_, pointRates) ->
                val rates = pointRates.map { rate ->
                    val sellProfit = getSellProfit(rate.sell, rateRanges.getValue(rate.currency).sell)
                    val buyProfit = getBuyProfit(rate.sell, rateRanges.getValue(rate.currency).buy)
                    Rate(
                        currency = Currency.values()[rate.currency - 2],
                        sellCost = Rate.Cost(rate.sell, sellProfit),
                        buyCost = Rate.Cost(rate.buy, buyProfit)
                    )
                }

                with (pointRates.first()) {
                    ExchangePoint(
                        city = city,
                        address = address,
                        name = name,
                        rates = rates,
                        isOpen = isOpen,
                        loadId = loadId,
                        loadTimestamp = loadTimestamp,
                        location = Pair(location[0], location[1]),
                        phone = phone,
                        time = time
                    )
                }
            }
    }

    private fun getSellProfit(sellCost: Float, range: ClosedRange<Float>): Rate.Profit {
        val rangeLength = range.endInclusive - range.start
        return when {
            sellCost <= range.start + rangeLength * 0.3f -> Rate.Profit.BEST
            sellCost <= range.start + rangeLength * 0.6f -> Rate.Profit.MEDIUM
            else -> Rate.Profit.WORST
        }
    }

    private fun getBuyProfit(buyCost: Float, range: ClosedRange<Float>): Rate.Profit {
        val rangeLength = range.endInclusive - range.start
        return when {
            buyCost >= range.start + rangeLength * 0.6f -> Rate.Profit.BEST
            buyCost >= range.start + rangeLength * 0.3f -> Rate.Profit.MEDIUM
            else -> Rate.Profit.WORST
        }
    }

    private class RateRange(
        val sell: ClosedRange<Float>,
        val buy: ClosedRange<Float>
    )
}