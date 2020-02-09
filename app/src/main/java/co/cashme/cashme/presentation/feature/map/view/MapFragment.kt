package co.cashme.cashme.presentation.feature.map.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import co.cashme.cashme.R
import co.cashme.cashme.presentation.base.BaseFragment
import com.google.android.gms.maps.SupportMapFragment
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

        childFragmentManager
            .beginTransaction()
            .replace(R.id.root, mapFragment)
            .commitNow()

        mapFragment.getMapAsync { googleMap ->
            viewModel.points.observe(this, Observer { points ->
                points.forEach {
                    googleMap.addMarker(it)
                }
            })
        }
    }
}
