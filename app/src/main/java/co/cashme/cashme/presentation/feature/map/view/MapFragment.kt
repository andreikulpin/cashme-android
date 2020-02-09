package co.cashme.cashme.presentation.feature.map.view

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import co.cashme.cashme.R
import co.cashme.cashme.presentation.base.BaseFragment
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.fragment_map.*
import javax.inject.Inject

internal class MapFragment : BaseFragment(R.layout.fragment_map) {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    private lateinit var viewModel: MapViewModel

    private val mapFragment: SupportMapFragment by lazy {
        SupportMapFragment.newInstance()
    }

    override fun initView() {
        super.initView()
        viewModel = ViewModelProvider(viewModelStore, viewModelProviderFactory)
            .get(MapViewModel::class.java)

        initCurrenciesView()
        initActionsView()

        childFragmentManager
            .beginTransaction()
            .replace(R.id.mapContainer, mapFragment)
            .commitNow()

        mapFragment.getMapAsync { googleMap ->
            viewModel.points.observe(this, Observer { points ->
                googleMap.clear()
                points.forEach {
                    googleMap.addMarker(it)
                }
            })
        }
    }

    private fun initCurrenciesView() {
        viewModel.currencies.observe(this, Observer { currencies ->
            currencyRadioGroup.items = currencies
        })

        currencyRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.onCurrencySelected(checkedId)
        }
    }

    private fun initActionsView() {
        viewModel.actions.observe(this, Observer { actions ->
            actionRadioGroup.items = actions
        })

        actionRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.onActionSelected(checkedId)
        }
    }

    override fun setupForInsets() {
        super.setupForInsets()
        controls.setOnApplyWindowInsetsListener { view, insets ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.systemWindowInsetTop
            }
            insets
        }
    }
}
