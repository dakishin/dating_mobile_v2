package com.dating.api

import android.os.Looper
import android.util.Log
import android.util.MalformedJsonException
import com.dating.model.NearUser
import com.dating.model.TelegramUser
import com.dating.model.Treba
import com.dating.model.TrebaType
import com.dating.modules.ProfilePreferences
import com.dating.util.ErrorCode
import com.dating.util.PifException
import com.dating.util.Utils
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.telegram.messenger.BuildConfig
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class DatingApi @Inject constructor() {
    private val service: PifService
    private val geoService: GeoService
    private val TAG = DatingApi::class.java.name

    @Inject
    lateinit var profilePreferences: ProfilePreferences

    init {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .addInterceptor(UserAgentInterceptor())
            .build()


        val mapper = ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)


        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .baseUrl(BuildConfig.SERVER_URL)
            .client(client)
            .build()


        service = retrofit.create(PifService::class.java)


        val geoRetrofit = Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .client(client)
            .build()

        geoService = geoRetrofit.create(GeoService::class.java)
    }


    fun getCityByLocation(lat: Double, lon: Double): String? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw RuntimeException("вы не должны использовать главный поток")
        }
        try {
            val response = geoService
                .decodeAddress("AIzaSyAQDKe5g__ZimJCC1-dYowbQMJjaQN_oGA", lat.toString() + "," + lon, Utils.getLanguage())
                .execute()
            val body = response.body()
            val country = body.country
            val city = body.city
            val countryCode = body.countryCode
            return city

        } catch (e: Throwable) {
            Log.e(TAG, e.message, e)
//            StatisticUtil.logError(" ошибка декодирования координаты  lat " + geoData.lat + " lon " + geoData.lon, e)
        }
//
        return null

    }


    @Throws(PifException::class)
    fun getUserTrebs(userUuid: String): List<Treba>? {
        return executeApiMethod(createService().getTrebas(userUuid)).result
    }

    @Throws(PifException::class)
    fun registerTelegramUser(telegramId: Long, firstName: String?, lastName: String?): TelegramUser? {
        return executeApiMethod(createService()
            .registerTelegramUser(PifService.RegisterTelegramUserParam(telegramId.toString(), firstName, lastName))).result
    }

    @Throws(PifException::class)
    fun createTreba(owner: TelegramUser, type: TrebaType, names: List<String>, priestUuid: String) {
        executeApiMethod(createService()
            .createTreba(PifService.CreateTrebaParam(owner.uuid, names, type, priestUuid)))
    }

    @Throws(PifException::class)
    fun sendGeoData(ownerUuid: String, lat: Double, lon: Double, city: String?) {
        executeApiMethod(createService()
            .sendGeoData(PifService.GeoDataParam(ownerUuid, lat, lon, city)))
    }

    @Throws(PifException::class)
    fun getNearMeUsers(): Observable<List<NearUser>> =
        createService()
            .searchNear(profilePreferences.getTelegramId()!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { responce -> Observable.just(responce.result ?: arrayListOf()) }


    private inner class UserAgentInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()
            request = request.newBuilder()
                .addHeader("User-Agent", getUserAgent())
                .build()
            return chain.proceed(request)
        }
    }

    private fun getUserAgent(): String {
        return "TelegramID:" + profilePreferences.getTelegramId() + " / VersionCode:" + BuildConfig.VERSION_CODE
    }

    private fun createService(): PifService {
        return service
    }

    @Throws(PifException::class)
    fun <T> executeApiMethod(call: Call<PifResponse<T>>): PifResponse<T> {

        try {
            val response = call.execute()
            if (!response.isSuccessful()) {
                Log.e(TAG, response.raw().toString())
                throw PifException(ErrorCode.UN_EXPECTED, response.raw().toString(), null)
            }

            val body = response.body()
            if (body.errorCode == null) {
                body.errorCode = ErrorCode.UN_EXPECTED
            }

            if (body.errorCode !== ErrorCode.OK) {
                throw PifException(body.errorCode, response.raw().toString(), null)
            }

            if (body.apiClientVersionCode > BuildConfig.VERSION_CODE) {
                throw PifException(ErrorCode.NEED_UPDATE, response.raw().toString(), null)
            }

            return body
        } catch (e: MalformedJsonException) {
            Log.e(TAG, e.message, e)
//            StatisticUtil.logError("невалидный джейсон в ответе сервера ", e)
            throw PifException(ErrorCode.UN_EXPECTED, e.message, e)
        } catch (e: PifException) {
            throw e
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
            throw PifException(ErrorCode.IO_ERROR, e.message, null)
        } catch (e: Throwable) {
//            StatisticUtil.logError("ошибка апи метода", e)
            Log.e(TAG, e.message, e)
            throw PifException(ErrorCode.UN_EXPECTED, e.message, e)
        }

    }


}