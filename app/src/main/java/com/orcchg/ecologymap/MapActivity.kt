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
        val POINT_1 = LatLng(57.5727, 40.1540)
        val POINT_2 = LatLng(57.6474, 40.2075)
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
                val marker = MarkerItem(selectedMarker!!.tag as String, location = selectedMarker!!.position)
                Util.delay({ showMarkerInfo(marker) }, 100)
            }
        }

        newLocation = Util.coverPositionsBest(POINTS, 11f)
        Util.delay({ run(newLocation) }, delay = 5000)
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
        markers().doOnNext { MarkerUtil.addMarker(map, it.location, it.tag) }

//    private fun processMarkers(): Flowable<MarkerItem> =
//        markers(init = 600, period = 5000)
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext {
//                    hideMarkerInfo(it)
//                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(it.location, 12f))
//                    Util.delay({ showMarkerInfo(it) })
//                }

    // ------------------------------------------
    private fun markers(init: Long = 150, period: Long = 350): Flowable<MarkerItem> =
            Flowable.interval(init, period, TimeUnit.MILLISECONDS)
                    .map { it -> MarkerItem(tag = "point_$it", location = POINTS[it.toInt()]) }
                    .take(POINTS.size.toLong())
                    .observeOn(AndroidSchedulers.mainThread())

//    private fun hideMarkerInfo(marker: MarkerItem) {
//        val dialog = supportFragmentManager.findFragmentByTag("point_${marker.position}") as? InfoDialog
//        dialog?.dismiss()
//    }

    private fun showMarkerInfo(marker: MarkerItem) {
        val payload = when (marker.tag) {
            "point_0" -> Payload(descriptionId = R.string.comment_point_0, waterDescId = R.string.water_point_0,
                    imageIds = listOf(R.drawable.image1s, R.drawable.image2s, R.drawable.image5s, R.drawable.image6),
                    statusIds = listOf(R.drawable.ic_baseline_wifi_off_24px, R.drawable.ic_baseline_camp, R.drawable.ic_baseline_restaurant_24px))
            "point_1" -> Payload(descriptionId = R.string.comment_point_1, waterDescId = R.string.water_point_1,
                    imageIds = listOf(R.drawable.image3s, R.drawable.image7, R.drawable.image8, R.drawable.image9),
                    statusIds = listOf(R.drawable.ic_baseline_wifi_24px, R.drawable.ic_baseline_restaurant_24px, R.drawable.ic_baseline_local_gas_station_24px))
            else -> Payload(descriptionId = R.string.comment_point_2, waterDescId = R.string.water_point_2,
                    imageIds = listOf(R.drawable.image4s, R.drawable.image10, R.drawable.image11, R.drawable.image12),
                    statusIds = listOf(R.drawable.ic_baseline_wifi_24px, R.drawable.ic_baseline_camp, R.drawable.ic_baseline_restaurant_24px, R.drawable.ic_baseline_local_gas_station_24px))
        }
        InfoDialog.newInstance(payload).show(supportFragmentManager, "point_${marker.tag}")
    }
}
