package com.trixiesoft.mychallenge.ui

import android.location.Address
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker
import com.trixiesoft.mychallenge.R
import com.trixiesoft.mychallenge.api.FilmLocation
import com.trixiesoft.mychallenge.api.actors
import com.trixiesoft.mychallenge.util.Geocode
import com.trixiesoft.mychallenge.util.bindView
import com.trixiesoft.mychallenge.util.toSingleLine
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a [MovieListActivity]
 * in two-pane mode (on tablets) or a [MovieDetailActivity]
 * on handsets.
 */
class MovieDetailFragment : Fragment() {

    private var item: FilmLocation? = null
    private val sanFransiscoBounds = LatLngBounds(LatLng(37.702982, -122.539359), LatLng(37.838927, -122.359139))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey("film")) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = it.getParcelable("film")
            }
        }

        if (item == null)
            return
    }

    private var geocodeDisp: Disposable? = null
    private val mapView: MapView by bindView(R.id.map_view)
    private val titleView: TextView by bindView(R.id.title_view)
    private val locationView: TextView by bindView(R.id.location_view)
    private val locationDetailView: TextView by bindView(R.id.location_view_detail)
    private val producerView: TextView by bindView(R.id.director_view)
    private val writerView: TextView by bindView(R.id.writer_view)
    private val actorsView: TextView by bindView(R.id.actors_view)
    private var address: Address? = null
    private var map: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       return inflater.inflate(R.layout.movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Show the dummy content as text in a TextView.
        item?.let {
            titleView.text = "${it.title} (${it.releaseYear})"
            locationView.text = "${it.locations}"
            locationDetailView.text = ""
            producerView.text = getString(
                R.string.director,
                if (it.director.isNullOrBlank()) getString(R.string.not_provided) else it.director
            )
            writerView.text = getString(
                R.string.writer,
                if (it.writer.isNullOrBlank()) getString(R.string.not_provided) else it.writer
            )
            actorsView.text = getString(
                R.string.starring,
                if (it.actor1.isNullOrBlank()) getString(R.string.not_provided) else it.actors()
            )
        }
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { googleMap ->
            map = googleMap
            googleMap.setOnMapLoadedCallback {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(sanFransiscoBounds, 10))
                showMap()
            }
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isMapToolbarEnabled = true
            googleMap.uiSettings.isZoomGesturesEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        if (!item?.locations.isNullOrEmpty()) {
            geocodeDisp = Geocode.fromName(context!!, item?.locations!!, sanFransiscoBounds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    // mapable address
                    address = it
                    locationDetailView.text = "${it.toSingleLine()}"
                    showMap()
                }, {
                    Log.e("Movie Detail", "Error: ", it)
                    // error
                }, {
                    // no address found for location
                    locationDetailView.text = getString(R.string.location_not_found)
                    Log.e("Movie Detail", "No Address found " + item?.locations)
                })
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        geocodeDisp?.dispose()
    }

    private fun showMap() {
        if (address != null) {
            val location = LatLng(address!!.latitude, address!!.longitude)
            map?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(item?.title)
                    .snippet(item?.locations)
                    .icon(defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.5f))
        }
    }
}

