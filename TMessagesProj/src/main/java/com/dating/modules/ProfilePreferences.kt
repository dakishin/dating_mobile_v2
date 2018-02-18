package com.dating.modules

import android.content.Context
import android.util.Log
import com.dating.model.TelegramUser
import com.dating.util.BasePreferences
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class ProfilePreferences @Inject constructor(context: Context) {

    private val PROFILE_KEY = "PROFILE_KEY"
    private val TELEGRAM_ID_KEY = "TELEGRAM_ID_KEY_v4"
    private val FIRST_NAME_KEY = "FIRST_NAME_KEY"
    private val LAST_NAME_KEY = "LAST_NAME_KEY"
    private val HAS_SEARCH_PURCHASE = "HAS_SEARCH_PURCHASE"

    private val TAG = ProfilePreferences::class.java.name
    val basePreferences: BasePreferences = BasePreferences(context, "ProfilePreferences")



    fun getTelegramId(): Int? {
        return basePreferences.getInt(TELEGRAM_ID_KEY)
    }

    fun saveTelegramId(uuid: Int) {
        basePreferences.setInt(TELEGRAM_ID_KEY, uuid)
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


    fun hasSearchPurchase(): Boolean {
        return basePreferences.getBoolean(HAS_SEARCH_PURCHASE, false)
    }

    fun saveHasSearchPurchase(value:Boolean) {
        basePreferences.setBoolean(HAS_SEARCH_PURCHASE, value)
    }


}