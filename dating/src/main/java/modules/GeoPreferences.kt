package modules

import android.content.Context
import module.christian.ru.dating.util.BasePreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class GeoPreferences @Inject constructor(val context: Context) {
    private val LAT_KEY = "LAT_KEY"
    private val LON_KEY = "LON_KEY"

    private val TAG = GeoPreferences::javaClass.name

    val basePreferences: BasePreferences = BasePreferences(context, "GeoPreferences")

    fun saveGeoData(lat: Double, lon: Double) {
        basePreferences.setFloat(LAT_KEY, lat.toFloat())
        basePreferences.setFloat(LON_KEY, lon.toFloat())
    }

    fun getLat(): Double? {
        return basePreferences.getFloat(LAT_KEY)?.toDouble()
    }

    fun getLon(): Double? {
        return basePreferences.getFloat(LON_KEY)?.toDouble()
    }


}