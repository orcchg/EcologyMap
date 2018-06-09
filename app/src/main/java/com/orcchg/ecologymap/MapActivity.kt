package com.orcchg.ecologymap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        val POINT_1 = LatLng(57.5495, 40.1608)
        val POINT_2 = LatLng(57.6494, 40.2075)
        val POINT_3 = LatLng(57.6715, 40.3681)
    }

    private lateinit var map: GoogleMap
    private lateinit var POINTS: List<LatLng>

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

        val route = LatLng(57.6987, 40.4272)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(route, 8.5f))

        val newLocation = Util.coverPositionsBest(POINTS, 11f)
        Util.delay({ run(newLocation) }, delay = 2000)
    }

    // ------------------------------------------
    private fun run(newLocation: CameraUpdate) {
        map.animateCamera(newLocation)
        putMarkers()
    }

    // ------------------------------------------
    private fun putMarkers() {
        Flowable.interval(150, 350, TimeUnit.MILLISECONDS)
                .map { it -> POINTS[it.toInt()] }
                .take(POINTS.size.toLong())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { MarkerUtil.addMarker(map, it) }
                .subscribe()
    }
}
