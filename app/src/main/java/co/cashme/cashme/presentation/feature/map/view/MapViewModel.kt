package co.cashme.cashme.presentation.feature.map.view

import androidx.lifecycle.*
import co.cashme.cashme.data.repository.ExchangePointsRepository
import co.cashme.cashme.domain.model.Currency
import co.cashme.cashme.domain.model.ExchangeAction
import co.cashme.cashme.domain.model.ExchangePoint
import co.cashme.cashme.domain.model.Result
import co.cashme.cashme.presentation.custom.mapradiogroup.MapRadioButtonModel
import co.cashme.cashme.presentation.feature.map.mapper.MarkerMapper
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val exchangePointsRepository: ExchangePointsRepository,
    private val markerMapper: MarkerMapper
) : ViewModel() {
    private val selectedCurrency: MutableLiveData<Currency> = MutableLiveData(Currency.USD)
    private val selectedAction: MutableLiveData<ExchangeAction> = MutableLiveData(ExchangeAction.SELL)

    val currencies: LiveData<List<MapRadioButtonModel>> = liveData {
        val currencies = Currency.values().map {
            MapRadioButtonModel(
                id = it.ordinal,
                title = it.name,
                checked = it == selectedCurrency.value
            )
        }
        emit(currencies)
    }

    val actions: LiveData<List<MapRadioButtonModel>> = liveData {
        val currencies = ExchangeAction.values().map {
            MapRadioButtonModel(
                id = it.ordinal,
                title = it.name,
                checked = it == selectedAction.value
            )
        }
        emit(currencies)
    }

    private val pointsSource: LiveData<List<ExchangePoint>> = liveData {
        when (val pointsResult = exchangePointsRepository.getExchangePoints()) {
            is Result.Success -> pointsResult.value.let { points ->
                Timber.d("Places (size=${points.size}): ${points.first()} ...")
                emit(points)
            }
            is Result.Error -> Timber.e(pointsResult.exception)
        }
    }

    val points = MediatorLiveData<List<MarkerOptions>>().apply {
        var currency: Currency = selectedCurrency.value!!
        var action: ExchangeAction = selectedAction.value!!
        var points: List<ExchangePoint>? = null

        val dispatch = { points: List<ExchangePoint>, currency: Currency, action: ExchangeAction ->
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
}
