package com.dating.ui.near

import com.dating.api.DatingApi
import com.dating.api.TelegramApi
import com.dating.model.CompoundUser
import com.dating.model.NearUser
import com.dating.interactors.ProfilePreferences
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.telegram.tgnet.TLRPC

/**
 *   Created by dakishin@gmail.com
 */
class NearMeListInteractor(val telegramApi: TelegramApi, val datingApi: DatingApi, val profilePreferences: ProfilePreferences) {

    fun loadUsers(chatId: Int): Observable<List<CompoundUser>>
        = telegramApi
        .getUsersFromChat(chatId)
        .zipWith(datingApi.getNearMeUsers(), BiFunction<List<TLRPC.User>, List<NearUser>, Observable<CompoundUser>>
        { telegramUsersParam, nearUsersParam ->

            val meTelegramId = profilePreferences.getTelegramId() ?: 0

            val nearUsers = nearUsersParam.filter { it.telegramId != meTelegramId }
            val telegramUsers = telegramUsersParam.filter { (it.id != meTelegramId) && !it.bot }

            val usersWithCity = arrayListOf<CompoundUser>()
            val usersWithoutCity = arrayListOf<CompoundUser>()

            nearUsers.forEach {
                val near = it
                val find = telegramUsers.findLast { it.id == near.telegramId }
                if (find != null) {
                    usersWithCity.add(CompoundUser(near, find))
                }
            }
            telegramUsers.forEach {
                val telegamUser = it
                if (usersWithCity.findLast { it.telegramUser.id == telegamUser.id } == null) {
                    usersWithoutCity.add(CompoundUser(null, telegamUser))
                }

            }

            Observable.fromIterable(usersWithCity.apply { this.addAll(usersWithoutCity) })
        }
        )

        .flatMap { v -> v }
        .toList()
        .toObservable()

}


