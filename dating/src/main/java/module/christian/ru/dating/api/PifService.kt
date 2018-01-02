package module.christian.ru.dating.api

import module.christian.ru.dating.model.TelegramUser
import module.christian.ru.dating.model.Treba
import module.christian.ru.dating.model.TrebaType
import module.christian.ru.dating.util.ErrorCode
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

    @POST("api/api_v3/registerTelegramUser")
    @Headers("Content-type: application/json")
    fun registerTelegramUser(@Body param: RegisterTelegramUserParam): Call<PifResponse<TelegramUser>>

    @POST("api/api_v3/sendGeoData")
    @Headers("Content-type: application/json")
    fun sendGeoData(@Body param: GeoDataParam): Call<PifResponse<Any>>



    class CreateTrebaParam(var userUuid: String?, var names: List<String>, var type: TrebaType, val priestUuid: String)
    class RegisterTelegramUserParam(val telegramId: String, val firstName: String?, val lastName: String?)
    class GeoDataParam(val ownerUuid: String, val lat: Double, val lon: Double, val city: String?)


}
