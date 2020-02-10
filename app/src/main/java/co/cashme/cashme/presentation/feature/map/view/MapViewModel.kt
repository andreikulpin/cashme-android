package co.cashme.cashme.presentation.feature.map.view

import androidx.lifecycle.*
import co.cashme.cashme.data.repository.ExchangePointsRepository
import co.cashme.cashme.domain.model.Currency
import co.cashme.cashme.domain.model.ExchangeAction
import co.cashme.cashme.domain.model.ExchangePoint
import co.cashme.cashme.domain.model.Result
import co.cashme.cashme.presentation.custom.mapradiogroup.MapRadioButtonModel
import co.cashme.cashme.presentation.feature.map.mapper.MapViewModelMapper
import co.cashme.cashme.presentation.feature.map.mapper.MarkerMapper
import co.cashme.cashme.presentation.feature.map.model.MarkerOptionsWrapper
import co.cashme.cashme.presentation.feature.map.model.PointInfoModel
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val exchangePointsRepository: ExchangePointsRepository,
    private val markerMapper: MarkerMapper,
    private val mapViewModelMapper: MapViewModelMapper
) : ViewModel() {
    private companion object {
        val DEFAULT_POSITION = LatLng(55.755855, 37.617655)
        const val DEFAULT_ZOOM_CITY = 11f
        const val DEFAULT_ZOOM_POINT = 16f
    }

    private val selectedCurrency: MutableLiveData<Currency> = MutableLiveData(Currency.USD)
    private val selectedAction: MutableLiveData<ExchangeAction> = MutableLiveData(ExchangeAction.SELL)

    val progress = MutableLiveData<Boolean>(false)

    val mapPositionWithZoom = MutableLiveData<Pair<LatLng, Float>>(DEFAULT_POSITION to DEFAULT_ZOOM_CITY)

    val currencies: LiveData<List<MapRadioButtonModel>> = liveData {
        emit(mapViewModelMapper.mapCurrencies(selectedCurrency.value!!))
    }

    val actions: LiveData<List<MapRadioButtonModel>> = liveData {
        emit(mapViewModelMapper.mapExchangeActions(selectedAction.value!!))
    }

    private val pointsSource: LiveData<List<ExchangePoint>> = liveData {
        progress.value = true
        when (val pointsResult = exchangePointsRepository.getExchangePoints()) {
            is Result.Success -> pointsResult.value.let { points ->
                emit(points)
            }
            is Result.Error -> Timber.e(pointsResult.exception)
        }
        progress.value = false
    }

    val points = MediatorLiveData<List<MarkerOptionsWrapper>>().apply {
        var currency: Currency = selectedCurrency.value!!
        var action: ExchangeAction = selectedAction.value!!
        var points: List<ExchangePoint>? = null

        val dispatch = fun(points: List<ExchangePoint>, currency: Currency, action: ExchangeAction) {
            value = markerMapper.mapMarkers(points, currency, action)
        }

        addSource(selectedCurrency) { selectedCurrency ->
            currency = selectedCurrency
            points?.let { dispatch(it, currency, action)}
        }

        addSource(selectedAction) { selectedAction ->
            action = selectedAction
            points?.let { dispatch(it, currency, action)}
        }

        addSource(pointsSource) { newPoints ->
            points = newPoints
            dispatch(newPoints, currency, action)
        }
    }

    val pointDetails = MutableLiveData<PointInfoModel>()

    fun onCurrencySelected(itemId: Int) {
        currencies.value?.find { it.id == itemId }
            ?.let {
                selectedCurrency.value = Currency.values()[it.id]
            }
    }

    fun onActionSelected(itemId: Int) {
        actions.value?.find { it.id == itemId }
            ?.let {
                selectedAction.value = ExchangeAction.values()[it.id]
            }
    }

    fun onMarkerClick(id: String) {
        pointsSource.value?.find { it.id == id }?.let { point ->
            pointDetails.value = mapViewModelMapper.mapPointInfo(point)

            mapPositionWithZoom.value = Pair(
                LatLng(point.location.first, point.location.second),
                DEFAULT_ZOOM_POINT
            )
        }
    }
}
