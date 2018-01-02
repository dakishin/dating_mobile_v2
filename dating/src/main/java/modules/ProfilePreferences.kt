package modules

import android.content.Context
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
class ProfilePreferences @Inject constructor(context: Context) {

    private val PROFILE_KEY = "PROFILE_KEY"
    private val TELEGRAM_ID_KEY = "TELEGRAM_ID_KEY"
    private val FIRST_NAME_KEY = "FIRST_NAME_KEY"
    private val LAST_NAME_KEY = "LAST_NAME_KEY"
    private val UUID_KEY = "UUID_KEY"


    private val TAG = ProfilePreferences::javaClass.name
    val basePreferences: BasePreferences = BasePreferences(context, "ProfilePreferences")


    fun getUUID(): String? {
        return basePreferences.getString(UUID_KEY, null)
    }

    fun saveUUID(uuid: String?) {
        basePreferences.setString(UUID_KEY, uuid)
    }

    fun getTelegramId(): String? {
        return basePreferences.getString(TELEGRAM_ID_KEY, null)
    }

    fun saveTelegramId(uuid: String) {
        basePreferences.setString(TELEGRAM_ID_KEY, uuid)
    }

    fun getLastName(): String? {
        return basePreferences.getString(LAST_NAME_KEY, null)
    }

    fun saveLastName(uuid: String?) {
        basePreferences.setString(LAST_NAME_KEY, uuid)
    }

    fun getFirstName(): String? {
        return basePreferences.getString(FIRST_NAME_KEY, null)
    }

    fun saveFistName(uuid: String?) {
        basePreferences.setString(FIRST_NAME_KEY, uuid)
    }


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