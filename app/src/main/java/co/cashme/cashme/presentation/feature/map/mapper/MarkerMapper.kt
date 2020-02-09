package co.cashme.cashme.presentation.feature.map.mapper

import co.cashme.cashme.domain.model.Currency
import co.cashme.cashme.domain.model.ExchangeAction
import co.cashme.cashme.domain.model.ExchangePoint
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject

class MarkerMapper @Inject constructor(
    private val markerBitmapDelegate: MarkerBitmapDelegate
) {
    fun mapMarkers(points: List<ExchangePoint>,
                   currency: Currency,
                   action: ExchangeAction): List<MarkerOptions> {
        return points.mapNotNull { point -> mapPoint(point, currency, action) }
    }

    private fun mapPoint(point: ExchangePoint,
                         currency: Currency,
                         action: ExchangeAction): MarkerOptions? {
        val rate = point.rates.find { it.currency == currency } ?: return null
        val cost = when (action) {
            ExchangeAction.SELL -> rate.sellCost
            ExchangeAction.BUY -> rate.buyCost
        }

        val latLng = LatLng(
            point.location.first,
            point.location.second
        )

        val bitmap = markerBitmapDelegate.makeMarkerBitmap(cost)

        return MarkerOptions()
            .position(latLng)
            .anchor(0.39f, 0.98f)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
    }
}