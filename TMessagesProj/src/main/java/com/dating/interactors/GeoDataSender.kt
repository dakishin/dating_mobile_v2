package com.dating.interactors

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.dating.api.DatingApi
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


/**
 *   Created by dakishin@gmail.com
 */
open class GeoDataSender constructor(val context: Context, val geoPreferences: GeoPreferences,
                                     val api: DatingApi, val locationInteractor: LocationInteractor,
                                     val profilePreferences: ProfilePreferences, val timeoutScheduler: Scheduler) {

    val TAG = GeoDataSender::class.java.name

    private var lastUpdateDate: Long? = null

    private val _24_HOURS = 1000 * 60 * 60 * 24

    var geoPermissionGranted: PublishSubject<Unit> = PublishSubject.create()


    open fun sendGeoData(): Completable =
        Completable
            .fromObservable(
                Observable
                    .fromCallable {
                        profilePreferences.getTelegramId()
                            ?: throw RuntimeException("empty telegramId")
                        Log.d(TAG, "get location requested")
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
            )
            .doOnError { exception ->
                Log.e(TAG, exception.message, exception)
            }
            .timeout(10, TimeUnit.SECONDS, timeoutScheduler)
            .onErrorComplete()


    @SuppressLint("MissingPermission")
    open fun sendGeoDataIfNeeded(): Completable {
        val lastUpdateDate = this.lastUpdateDate
        if (lastUpdateDate != null && (timeoutScheduler.now(TimeUnit.MILLISECONDS) < lastUpdateDate + _24_HOURS)) {
            return Completable.complete()
        }
        return sendGeoData()
    }

    fun notifyPermissionGranted() {
        geoPermissionGranted.onNext(Unit)
    }

    fun hasLocation() = geoPreferences.getLat() != null


}