package com.orcchg.ecologymap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MapActivity : AppCompatActivity(), OnMapReadyCallback, InfoDialog.Callback {

    companion object {
        val POINT_1 = LatLng(57.5495, 40.1608)
        val POINT_2 = LatLng(57.6494, 40.2075)
        val POINT_3 = LatLng(57.6715, 40.3681)
    }

    internal var forceMove = false
    internal var selectedMarker: Marker? = null
    internal lateinit var newLocation: CameraUpdate
    private lateinit var map: GoogleMap
    private lateinit var POINTS: List<LatLng>
    private val ROUTE = LatLng(57.6987, 40.4272)

    // ------------------------------------------
    private fun initData() {
        POINTS = listOf(POINT_1, POINT_2, POINT_3)
    }

    // ------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        initData()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ROUTE, 8.5f))
        map.setOnMapClickListener { onDismiss() }
        map.setOnMarkerClickListener {
            selectedMarker = it
            Util.moveToMarker(map, it)
            true
        }
        map.setOnCameraMoveStartedListener {
            forceMove = it == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION
        }
        map.setOnCameraIdleListener {
            if (forceMove && selectedMarker != null) {
                Util.delay({ showMarkerInfo(MarkerItem(0, location = selectedMarker!!.position)) }, 100)
            }
        }

        newLocation = Util.coverPositionsBest(POINTS, 11f)
        Util.delay({ run(newLocation) }, delay = 2000)
    }

    override fun onDismiss() {
        selectedMarker = null
        map.animateCamera(newLocation)
    }

    // ------------------------------------------
    private fun run(newLocation: CameraUpdate) {
        map.animateCamera(newLocation)
//        putMarkers().doOnComplete {
//            processMarkers()
//                // restore initial map location
//                .delay(500, TimeUnit.MILLISECONDS)
//                .doOnComplete { Util.delay({ map.animateCamera(newLocation) }) }
//                .subscribe()
//        }.subscribe()
        putMarkers().subscribe()
    }

    // ------------------------------------------
    private fun putMarkers(): Flowable<MarkerItem> =
        markers().doOnNext { MarkerUtil.addMarker(map, it.location) }

    private fun processMarkers(): Flowable<MarkerItem> =
        markers(init = 600, period = 5000)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    hideMarkerInfo(it)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(it.location, 12f))
                    Util.delay({ showMarkerInfo(it) })
                }

    // ------------------------------------------
    private fun markers(init: Long = 150, period: Long = 350): Flowable<MarkerItem> =
            Flowable.interval(init, period, TimeUnit.MILLISECONDS)
                    .map { it -> MarkerItem(it, location = POINTS[it.toInt()]) }
                    .take(POINTS.size.toLong())
                    .observeOn(AndroidSchedulers.mainThread())

    private fun hideMarkerInfo(marker: MarkerItem) {
        val dialog = supportFragmentManager.findFragmentByTag("point_${marker.position}") as? InfoDialog
        dialog?.dismiss()
    }

    private fun showMarkerInfo(marker: MarkerItem) {
        InfoDialog.newInstance().show(supportFragmentManager, "point_${marker.position}")
    }
}
