package modules

import android.util.Log
import module.christian.ru.dating.api.Api
import module.christian.ru.dating.util.PifException
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

    fun registerAsync(telegramId: Long, firstName: String?, lastName: String?) {
        Thread(Runnable {
            try {
                val user = api.registerTelegramUser(telegramId.toString(), firstName, lastName)
                profilePreferences.saveTelegramId(telegramId)
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