package com.dating.activity.near

import com.dating.api.DatingApi
import com.dating.api.TelegramApi
import com.dating.model.CompoundUser
import com.dating.model.NearUser
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.telegram.tgnet.TLRPC

/**
 *   Created by dakishin@gmail.com
 */
class NearMeListInteractor(val telegramApi: TelegramApi, val datingApi: DatingApi) {

    fun loadUsers(chatId: Int): Observable<List<CompoundUser>> {
//        val chat_id = activity.resources.getInteger(R.integer.living_room_id)
        return telegramApi
            .getUsersFromChat(chatId)
            .zipWith(datingApi.getNearMeUsers(), BiFunction<List<TLRPC.User>, List<NearUser>, Observable<CompoundUser>>
            { telegramUsers, nearUsers ->

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
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { users ->
//                    try {
//
//                        mUserAdapter.add(users)
//                    } catch (e: Throwable) {
//                        (this@NearMeListFragment::errorHandle)()(e)
//                    }
//
//                }, errorHandle)


    }


}