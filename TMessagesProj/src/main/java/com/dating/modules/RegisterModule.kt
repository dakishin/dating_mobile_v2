package com.dating.modules

import android.content.Context
import android.util.Log
import com.dating.api.DatingApi
import com.dating.util.PifException
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class RegisterModule @Inject constructor(context: Context) {
    @Inject lateinit var api: DatingApi
    @Inject lateinit var profilePreferences: ProfilePreferences

    val TAG = RegisterModule::javaClass.name

    fun registerAsync(telegramId: Long, firstName: String?, lastName: String?) {
        Thread(Runnable {
            try {
                val user = api.registerTelegramUser(telegramId, firstName, lastName)
                profilePreferences.saveTelegramId(telegramId)
                profilePreferences.saveUUID(user?.uuid)
                profilePreferences.saveFistName(firstName)
                profilePreferences.saveLastName(lastName)
            } catch (e: PifException) {
                Log.e(TAG, e.error.toString(), e)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }

        }).start()
    }
}