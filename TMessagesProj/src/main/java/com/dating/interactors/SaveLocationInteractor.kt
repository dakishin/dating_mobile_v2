package com.dating.interactors

import android.annotation.SuppressLint
import android.util.Log
import com.dating.api.DatingApi
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


/**
 *   Created by dakishin@gmail.com
 */
open class SaveLocationInteractor constructor(
    private val geoPreferences: GeoPreferences,
    private val api: DatingApi,
    private val locationInteractor: LocationInteractor,
    private val profilePreferences: ProfilePreferences,
    private val timeoutScheduler: Scheduler,
    private val geoPermissionGranted: PublishSubject<Unit>) {

    val TAG = SaveLocationInteractor::class.java.name

    private var lastUpdateDate: Long? = null

    private val _24_HOURS = 1000 * 60 * 60 * 24


    open fun saveLocation(): Observable<Unit> =
        Observable
            .fromCallable {
                profilePreferences.getTelegramId()
                    ?: throw RuntimeException("empty telegramId")
                locationInteractor.getLocation()
            }
            .flatMap { v -> v }
            .map { optional ->
                if (optional.empty()) {
                    throw RuntimeException("empty optional")
                }
                this.lastUpdateDate = timeoutScheduler.now(TimeUnit.MILLISECONDS)
                geoPreferences.saveGeoData(optional.value!!.latitude, optional.value.longitude)
                val city = api.getCityByLocation(optional.value.latitude, optional.value.longitude)
                api.sendGeoData(profilePreferences.getTelegramId()!!, optional.value.latitude, optional.value.longitude, city)
            }
            .doOnError { exception ->
                Log.e(TAG, exception.message, exception)
            }
            .onErrorReturnItem(Unit)
            .timeout(10, TimeUnit.SECONDS, timeoutScheduler)
            .onErrorReturnItem(Unit)


    @SuppressLint("MissingPermission")
    open fun saveLocationIfNeeded() =
        Observable.fromCallable {
            val lastUpdateDate = this.lastUpdateDate
            if (lastUpdateDate != null && (timeoutScheduler.now(TimeUnit.MILLISECONDS) < lastUpdateDate + _24_HOURS)) {
                Observable.just(Unit)
            } else {
                saveLocation()
            }
        }
            .flatMap { v -> v }
            .onErrorReturnItem(Unit)


    open fun notifyPermissionGranted() {
        geoPermissionGranted.onNext(Unit)
    }

    open fun saveLocationWhenPermissionGranted(): Observable<Unit> =
        geoPermissionGranted
            .flatMap {
                saveLocation()
            }
            .onErrorReturnItem(Unit)


    open fun hasLocation() = geoPreferences.getLat() != null


}