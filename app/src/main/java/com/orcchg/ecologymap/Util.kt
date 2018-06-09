package com.orcchg.ecologymap

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object Util {

    const val DEFAULT_ZOOM_LEVEL = 10f

    fun delay(action: () -> Unit, delay: Long = 500,
              observer: Scheduler = AndroidSchedulers.mainThread()) {
        Flowable.just(10).delay(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(observer)
                .doOnComplete(action)
                .subscribe()
    }

    fun throttle(action: () -> Unit, delay: Long = 500,
                 observer: Scheduler = AndroidSchedulers.mainThread()) {
        Flowable.just(10).throttleFirst(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(observer)
                .doOnComplete(action)
                .subscribe()
    }

    // ------------------------------------------
    fun coverPositionsBest(positions: Collection<LatLng>, zoom: Float = 0f): CameraUpdate {
        val bounds = getBounds(positions)
        return if (zoom <= 0) {
            CameraUpdateFactory.newLatLngBounds(bounds, 0)
        } else {
            CameraUpdateFactory.newLatLngZoom(bounds.center, zoom)
        }
    }

    fun getBounds(positions: Collection<LatLng>): LatLngBounds {
        var boundsBuilder = LatLngBounds.Builder()
        for (position in positions) {
            boundsBuilder = boundsBuilder.include(position)
        }
        return boundsBuilder.build()
    }
}
