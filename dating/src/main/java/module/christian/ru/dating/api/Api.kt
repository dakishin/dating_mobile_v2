package module.christian.ru.dating.api

import android.os.Looper
import android.util.Log
import com.google.gson.stream.MalformedJsonException
import module.christian.ru.dating.BuildConfig
import module.christian.ru.dating.model.TelegramUser
import module.christian.ru.dating.model.Treba
import module.christian.ru.dating.model.TrebaType
import module.christian.ru.dating.util.ErrorCode
import module.christian.ru.dating.util.LocationUtils
import module.christian.ru.dating.util.PifException
import module.christian.ru.dating.util.Utils
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import rx.schedulers.Schedulers
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class Api @Inject constructor() {
    private val service: PifService
    private val connectionPool: ConnectionPool
    private val geoService: GeoService
    private val TAG = Api::class.java.name


    init {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor())
            .addInterceptor(logInterceptor).build()

        connectionPool = client.connectionPool()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.SERVER_URL)
            .client(client)
            .build()


        service = retrofit.create(PifService::class.java)


        val geoRetrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .client(client)
            .build()

        geoService = geoRetrofit.create(GeoService::class.java)
    }


    fun decodeAddress(geoData: LocationUtils.GeoData) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw RuntimeException("вы не должны использовать главный поток")
        }

        try {
            val response = geoService.decodeAddress("AIzaSyAQDKe5g__ZimJCC1-dYowbQMJjaQN_oGA", geoData.lat.toString() + "," + geoData.lon, Utils.getLanguage())
                .execute()
            val body = response.body()
            geoData.country = body.getCountry()
            geoData.city = body.getCity()
            geoData.countryCode = body.getCountryCode()
            if (geoData.city == null) {
//                StatisticUtil.logInfo("ошибка декодирования координаты. lat " + geoData.lat + " lon " + geoData.lon)
            }
        } catch (e: Throwable) {
            Log.e(TAG, e.message, e)
//            StatisticUtil.logError(" ошибка декодирования координаты  lat " + geoData.lat + " lon " + geoData.lon, e)
        }

    }


    @Throws(PifException::class)
    fun getUserTrebs(userUuid: String): List<Treba>? {
        return executeApiMethod(createService().getTrebas(userUuid)).result
    }

    @Throws(PifException::class)
    fun registerTelegramUser(telegramId: String, firstName: String?, lastName: String?): TelegramUser? {
        return executeApiMethod(createService()
            .registerTelegramUser(PifService.RegisterTelegramUserParam(telegramId, firstName, lastName))).result
    }

    @Throws(PifException::class)
    fun createTreba(owner: TelegramUser?, type: TrebaType, names: List<String>, priestUuid: String) {
        val userUuid = owner?.uuid
        executeApiMethod(createService().createTreba(PifService.CreateTrebaParam(userUuid, names, type, priestUuid)))
    }

    @Throws(PifException::class)
    fun sendGeoData(lat: Double, lon: Double, city: String?) {
        executeApiMethod(createService().sendGeoData(PifService.GeoDataParam(lat, lon, city)))
    }


    enum class UserType {
        RUSSIANS,
        OTHERS
    }


    fun updateTelegramId(telegramId: Int?, userUuid: String) {
        Observable
            .fromCallable<Any> {
                //                executeApiMethod(createService().updateTelegramId(telegramId, userUuid))
                null
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }


    fun closeConnections() {
        try {
            connectionPool.evictAll()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }

    private inner class AuthInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()
//            val profile = PreferencesModule.getProfile()
//            if (profile != null && !Utils.isEmpty(profile!!.email)) {
//                request = request.newBuilder()
//                    .addHeader(AUTH_HEADER, URLEncoder.encode(profile!!.email, "utf-8"))
//                    .build()
//
//            }
            return chain.proceed(request)
        }
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

            if (body.apiClientVersionCode > Utils.getAppVersion()) {
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