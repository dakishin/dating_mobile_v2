package com.dating.interactors

import android.util.Log
import com.dating.api.DatingApi
import io.reactivex.Observable
import org.telegram.tgnet.TLRPC

/**
 *   Created by dakishin@gmail.com
 */
class RegisterInteractor(val api: DatingApi, val profilePreferences: ProfilePreferences, val geoDataSender: SaveLocationInteractor) {

    val TAG = RegisterInteractor::javaClass.name

    fun registerTelegramUser(telegramId: Int, firstName: String?, lastName: String?) =
        Observable
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
                geoDataSender.saveLocation()
            }
            .doOnError {
                Log.e(TAG, it.message, it)
            }
            .onErrorReturn { Unit }


    fun registerTelegramUser(user: TLRPC.User?) =
        Observable
            .fromCallable {
                registerTelegramUser(user!!.id, user.first_name, user.last_name)
            }
            .flatMap { v -> v }
            .doOnError {
                Log.e(TAG, it.message, it)
            }
            .onErrorReturn { Unit }

}