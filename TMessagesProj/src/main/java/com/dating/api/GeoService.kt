package com.dating.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *   Created by dakishin@gmail.com
 */
interface GeoService {

    @GET("geocode/json")
    fun decodeAddress(@Query("key") key: String,
                      @Query("latlng") latLon: String,
                      @Query("language") language: String): Call<GeoResult>

}