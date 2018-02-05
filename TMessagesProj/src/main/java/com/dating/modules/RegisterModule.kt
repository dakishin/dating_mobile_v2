package com.dating.modules

import android.content.Context
import com.dating.api.DatingApi
import com.dating.viper.IgnoreErrorsObserver
import io.reactivex.schedulers.Schedulers
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
        api.registerTelegramUser(telegramId, firstName, lastName)
            .map {
                profilePreferences.saveTelegramId(telegramId)
                profilePreferences.saveFistName(firstName)
                profilePreferences.saveLastName(lastName)
            }
            .subscribeOn(Schedulers.io())
            .subscribe(IgnoreErrorsObserver { })
    }
}