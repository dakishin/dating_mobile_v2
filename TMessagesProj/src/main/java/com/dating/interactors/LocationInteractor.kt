package com.dating.interactors

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.dating.util.Optional
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 *   Created by dakishin@gmail.com
 */
open class LocationInteractor(val context: Context, val timeoutScheduler: Scheduler,
                              val locationManager: LocationManager, val permissionInteractor: PermissionInteractor) {
    val TAG = "LocationInteractor"

    @SuppressLint("MissingPermission")
    open fun getLocation(): Observable<Optional<Location>> =
        Observable
            .fromCallable {
                if (!permissionInteractor.isGeoPermissionGranted()) {
                    throw RuntimeException("geo permission not granted")
                } else {
                    var lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    lastKnownLocation = lastKnownLocation
                        ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    Optional(lastKnownLocation)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                if (it.empty()) {
                    val provider = locationManager.getBestProvider(Criteria(), true)
                    provider ?: throw RuntimeException("location provider not found")
                    Observable.create<Optional<Location>> { publisher ->
                        val locationListener = object : LocationListener {
                            override fun onLocationChanged(location: Location) {
                                publisher.onNext(Optional(location))
                                publisher.onComplete()
                            }

                            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

                            override fun onProviderEnabled(provider: String) {}

                            override fun onProviderDisabled(provider: String) {}
                        }
                        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener)
                    }
                } else {
                    Observable.just(it)
                }
            }
            .observeOn(Schedulers.io())
            .doOnError {
                Log.e(TAG, it.message, it)
            }
            .onErrorReturnItem(Optional())
            .timeout(10, TimeUnit.SECONDS, timeoutScheduler)
            .onErrorReturnItem(Optional())
            .doOnSubscribe {
                Log.d(TAG, "get location requested")
            }
            .doOnNext {
                Log.d(TAG, "location is ${it.value}")
            }


}