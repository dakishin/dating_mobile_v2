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
    @Inject lateinit var preferencesModule: PreferencesModule

    val TAG = RegisterModule::javaClass.name

    fun registerAsync(telegramId: String, firstName: String?, lastName: String?) {
        Thread(Runnable {
            try {
                val user = api.registerTelegramUser(telegramId, firstName, lastName)
                preferencesModule.saveProfile(user)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }

        }).start()
    }
}