package com.dating.interactors

import android.content.Context
import com.dating.util.BasePreferences

/**
 *   Created by dakishin@gmail.com
 */
open class GeoPreferences (val context: Context) {
    private val LAT_KEY = "LAT_KEY"
    private val LON_KEY = "LON_KEY"

    private val TAG = GeoPreferences::javaClass.name

    val basePreferences: BasePreferences = BasePreferences(context, "GeoPreferences")

    open fun saveGeoData(lat: Double, lon: Double) {
        basePreferences.setFloat(LAT_KEY, lat.toFloat())
        basePreferences.setFloat(LON_KEY, lon.toFloat())
    }

   open  fun getLat(): Double? {
        return basePreferences.getFloat(LAT_KEY)?.toDouble()
    }

    fun getLon(): Double? {
        return basePreferences.getFloat(LON_KEY)?.toDouble()
    }


}