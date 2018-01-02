package modules

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.gms.location.LocationServices
import module.christian.ru.dating.api.Api
import javax.inject.Inject
import javax.inject.Singleton


/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class GeoModule @Inject constructor() {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var geoPreferences: GeoPreferences

    @Inject
    lateinit var profilePreferences: ProfilePreferences

    @Inject
    lateinit var api: Api

    val TAG = GeoModule::javaClass.name


    @SuppressLint("MissingPermission")
    fun sendGeoData() {
        Thread({
            sendGeoDataSync()
        }).start()
    }


    private fun sendGeoDataSync() {
        val uuid = profilePreferences.getUUID() ?: return
        if (geoPreferences.getLat() != null) {
            return
        }
        val grantedPermission = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (grantedPermission != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val lastLocation = mFusedLocationClient.lastLocation
        lastLocation
            ?.addOnSuccessListener { location ->
                location ?: return@addOnSuccessListener
                Thread({
                    try {
                        geoPreferences.saveGeoData(location.latitude, location.longitude)
                        val city = api.getCityByLocation(location.latitude, location.longitude)
                        api.sendGeoData(uuid, location.latitude, location.longitude, city)
                    } catch (e: Throwable) {
                        Log.e(TAG, e.message, e)
                    }
                }).start()
            }

    }

}