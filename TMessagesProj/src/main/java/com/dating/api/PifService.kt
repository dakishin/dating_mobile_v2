package com.dating.api

import com.dating.model.NearUser
import com.dating.model.TelegramUser
import com.dating.model.Treba
import com.dating.model.TrebaType
import com.dating.util.ErrorCode
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

/**
 *   Created by dakishin@gmail.com
 */

class PifResponse<T> {
    var errorCode: ErrorCode? = null
    var result: T? = null
    var apiClientVersionCode: Int = 0
    var isLastPage: Boolean = false
}

interface PifService {

    @POST("api/api_v3/getTrebas")
    @FormUrlEncoded
    @Headers("Content-type: application/x-www-form-urlencoded")
    fun getTrebas(@Field("uuid") uuid: String): Call<PifResponse<List<Treba>>>


    @POST("api/api_v3/createTreba")
    @Headers("Content-type: application/json")
    fun createTreba(@Body param: CreateTrebaParam): Call<PifResponse<Any>>


    @POST("api/api_v3/sendGeoDataIfNeeded")
    @Headers("Content-type: application/json")
    fun sendGeoData(@Body param: GeoDataParam): Call<PifResponse<Any>>

    @GET("api/api_v3/search/{telegramId}")
    @Headers("Content-type: application/json")
    fun searchNear(@Path("telegramId") telegramId: Long): Observable<PifResponse<List<NearUser>>>


    @POST("api/api_v3/registerTelegramUser")
    @Headers("Content-type: application/json")
    fun registerTelegramUser(@Body param: RegisterTelegramUserParam): Call<PifResponse<TelegramUser>>


    class CreateTrebaParam(var userUuid: String?, var names: List<String>, var type: TrebaType, val priestUuid: String)
    class RegisterTelegramUserParam(val telegramId: String, val firstName: String?, val lastName: String?)
    class GeoDataParam(val ownerUuid: String, val lat: Double, val lon: Double, val city: String?)


}