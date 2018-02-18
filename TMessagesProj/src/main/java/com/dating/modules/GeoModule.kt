package com.dating.modules

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.dating.api.DatingApi
import com.dating.util.Utils
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationAccuracy
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.rx.ObservableFactory
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class GeoModule @Inject constructor(context: Context) {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var geoPreferences: GeoPreferences

    @Inject
    lateinit var profilePreferences: ProfilePreferences

    @Inject
    lateinit var api: DatingApi

    val TAG = GeoModule::class.java.name

    private var lastUpdateDate: Long? = null

    private val _24_HOURS = 1000 * 60 * 60 * 24

    var geoPermissionGranted: PublishSubject<Unit> = PublishSubject.create()


    val sendGeoDataObserver =
        Observable.just(Unit)
            .map {
                profilePreferences.getTelegramId() ?: throw RuntimeException("empty telegramId")

                if (!Utils.isGeoPermissionGranted(context)) {
                    throw RuntimeException("need permission")
                }

                Log.d(TAG, "get location requested")
                ObservableFactory
                    .from(SmartLocation.with(context)
                        .location()
                        .config(LocationParams.Builder().setAccuracy(LocationAccuracy.HIGH).build())
                        .oneFix())
                    .observeOn(Schedulers.io())
            }
            .flatMap { v -> v }
            .map { location ->
                this.lastUpdateDate = System.currentTimeMillis()
                geoPreferences.saveGeoData(location.latitude, location.longitude)
                val city = api.getCityByLocation(location.latitude, location.longitude)
                api.sendGeoData(profilePreferences.getTelegramId()!!, location.latitude, location.longitude, city)
            }
            .doOnError { exception ->
                Log.e(TAG, exception.message, exception)
            }
            .subscribeOn(Schedulers.io())


    @SuppressLint("MissingPermission")
    fun sendGeoDataIfNeeded(): Observable<Unit> {
        val lastUpdateDate = this.lastUpdateDate
        if (lastUpdateDate != null && lastUpdateDate < System.currentTimeMillis() + _24_HOURS) {
            return Observable.just(Unit)
        }
        return sendGeoDataObserver
            .take(1)
            .timeout(20, TimeUnit.SECONDS)
    }

    fun notifyPermissionGranted() {
        geoPermissionGranted.onNext(Unit)
    }

    fun hasLocation() = geoPreferences.getLat() != null


}