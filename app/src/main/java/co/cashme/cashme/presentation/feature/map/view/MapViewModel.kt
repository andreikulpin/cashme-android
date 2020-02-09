package co.cashme.cashme.presentation.feature.map.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import co.cashme.cashme.data.repository.ExchangePointsRepository
import co.cashme.cashme.domain.model.Currency
import co.cashme.cashme.domain.model.ExchangeAction
import co.cashme.cashme.domain.model.Result
import co.cashme.cashme.presentation.feature.map.mapper.MarkerMapper
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val exchangePointsRepository: ExchangePointsRepository,
    private val markerMapper: MarkerMapper
) : ViewModel() {
    private val currency: Currency = Currency.USD
    private val action: ExchangeAction = ExchangeAction.SELL

    val points: LiveData<List<MarkerOptions>> = liveData {
        when (val pointsResult = exchangePointsRepository.getExchangePoints()) {
            is Result.Success -> pointsResult.value.let { points ->
                Timber.d("Places (size=${points.size}): ${points.first()} ...")
                val markers = markerMapper.mapMarkers(points, currency, action)
                emit(markers)
            }
            is Result.Error -> Timber.e(pointsResult.exception)
        }
    }
}
