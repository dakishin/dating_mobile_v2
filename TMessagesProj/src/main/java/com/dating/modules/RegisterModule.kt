package com.dating.modules

import com.dating.api.DatingApi
import com.dating.viper.IgnoreErrorsObserver
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.telegram.messenger.UserConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class RegisterModule @Inject constructor() {
    @Inject lateinit var api: DatingApi
    @Inject lateinit var profilePreferences: ProfilePreferences

    @Inject lateinit var geoModule: GeoModule

    val TAG = RegisterModule::javaClass.name

    fun registerTelegramUser(telegramId: Int, firstName: String?, lastName: String?): Observable<Unit> {
        return Observable
            .fromCallable {
                if (profilePreferences.getTelegramId() == telegramId) {
                    Observable.just(Unit)
                } else {
                    api.registerTelegramUser(telegramId, firstName, lastName)
                }
            }
            .flatMap { v -> v }
            .map {
                profilePreferences.saveTelegramId(telegramId)
                profilePreferences.saveFistName(firstName)
                profilePreferences.saveLastName(lastName)
            }
            .flatMap {
                geoModule.sendGeoDataObserver.take(1)
            }
    }

    fun registerTelegramUser() {
        Observable
            .fromCallable {
                val user = UserConfig.getCurrentUser()
                registerTelegramUser(user.id, user.first_name, user.last_name)
            }
            .flatMap { v -> v }
            .subscribeOn(Schedulers.io())
            .subscribe(IgnoreErrorsObserver {  })
    }
}