package modules

import android.util.Log
import module.christian.ru.dating.api.Api
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class RegisterModule @Inject constructor() {
    @Inject lateinit var api: Api
    @Inject lateinit var profilePreferences: ProfilePreferences

    val TAG = RegisterModule::javaClass.name

    fun registerAsync(telegramId: Long, jsonValue: String) {
        Thread(Runnable {
            try {
                api.registerTelegramUser(telegramId, jsonValue)
//                profilePreferences.saveTelegramId(telegramId)
//                profilePreferences.saveUUID(user?.uuid)
//                profilePreferences.saveFistName(firstName)
//                profilePreferences.saveLastName(jsonValue)
//                profilePreferences.saveAccessHash(accessHash)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }

        }).start()
    }
}