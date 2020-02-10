package co.cashme.cashme.presentation.feature.map.view

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import co.cashme.cashme.R
import co.cashme.cashme.presentation.base.BaseFragment
import co.cashme.cashme.utils.extensions.castTo
import co.cashme.cashme.utils.extensions.setVisibleOrGone
import co.cashme.cashme.utils.extensions.update
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_point_info.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.progress_fullscreen.*
import javax.inject.Inject

internal class MapFragment : BaseFragment(R.layout.fragment_map) {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    private lateinit var viewModel: MapViewModel

    private val mapFragment: SupportMapFragment by lazy {
        SupportMapFragment.newInstance()
    }

    private val mapPadding = MutableLiveData<Rect>(Rect())
    private lateinit var infoBottomSheetBehavior: BottomSheetBehavior<View>

    override fun initView() {
        super.initView()
        viewModel = ViewModelProvider(viewModelStore, viewModelProviderFactory)
            .get(MapViewModel::class.java)
        infoBottomSheetBehavior = BottomSheetBehavior.from(infoBottomSheet)
        initCurrenciesView()
        initActionsView()
        initMap()
        initInfoBottomSheet()
        viewModel.progress.observe(this, Observer(progress::setVisibleOrGone))
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

    private fun initMap() {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.mapContainer, mapFragment)
            .commitNow()

        mapFragment.getMapAsync { googleMap ->
            mapPadding.observe(this, Observer { padding -> with (padding) {
                googleMap.setPadding(left, top, right, bottom)
            }})

            viewModel.points.observe(this, Observer { points ->
                googleMap.clear()
                points.forEach {
                    googleMap.addMarker(it.markerOptions).tag = it.id
                }
            })

            viewModel.mapPositionWithZoom.observe(this, Observer { (position, zoom) ->
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom))
            })

            googleMap.setOnMarkerClickListener { marker ->
                marker.tag?.castTo<String>()?.let(viewModel::onMarkerClick)
                true
            }

            googleMap.setOnMapClickListener {
                infoBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun initInfoBottomSheet() {
        infoBottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            peekHeight = 0
            isHideable = true
            skipCollapsed = true
        }

        viewModel.pointDetails.observe(this, Observer { info ->
            pointTitleView.text = info.title
            ratesContainer.apply {
                removeAllViews()
                info.rates.forEach {
                    val text = TextView(context).apply {
                        text = it
                    }
                    addView(text)
                }
            }

            phoneTextView.text = info.phone
            addressTextView.text = info.address

            infoBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        })
    }

    override fun setupForInsets() {
        super.setupForInsets()
        controls.setOnApplyWindowInsetsListener { view, insets ->
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.systemWindowInsetTop
            }

            mapPadding.value?.let { padding ->
                padding.update(top = insets.systemWindowInsetTop)
                mapPadding.value = padding
            }

            insets
        }
    }
}
