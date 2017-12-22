package modules

import android.util.Log
import com.google.gson.Gson
import module.christian.ru.dating.model.TelegramUser
import module.christian.ru.dating.util.BasePreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class PreferencesModule @Inject constructor(){

    private val PROFILE_KEY = "PROFILE_KEY"
    private val TAG = PreferencesModule::javaClass.name

    @Inject
    lateinit var basePreferences: BasePreferences


    fun getProfile(): TelegramUser? {
        try {
            val content = basePreferences.getString(PROFILE_KEY, null) ?: return null
            return Gson().fromJson<TelegramUser>(content, TelegramUser::class.java)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            return null
        }

    }


    fun saveProfile(telegramUser: TelegramUser?) {
        val userJson: String?
        if (telegramUser == null) {
            userJson = null
        } else {
            userJson = Gson().toJson(telegramUser)
        }
        basePreferences.setString(PROFILE_KEY, userJson)
    }

}