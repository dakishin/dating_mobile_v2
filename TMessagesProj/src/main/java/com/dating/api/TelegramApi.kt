package com.dating.api

import android.util.Log
import io.reactivex.Observable
import org.telegram.messenger.MessagesController
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLRPC
import javax.inject.Inject
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */
@Singleton
class TelegramApi @Inject constructor() {

    private val TAG = TelegramApi::javaClass.name

    fun getUsersFromChat(chat_id: Int): Observable<List<TLRPC.User>> =
        Observable.create<List<TLRPC.User>> { observer ->
            val req = TLRPC.TL_channels_getParticipants()
            req.channel = MessagesController.getInputChannel(chat_id)
            req.filter = TLRPC.TL_channelParticipantsRecent()
            req.offset = 0
            req.limit = 300
            val reqId = ConnectionsManager.getInstance().sendRequest(req) { response, error ->

                if (error != null) {
                    Log.e(TAG, error.text)
                    observer.onError(RuntimeException("error get telegram users from group: " + error.text))
                    return@sendRequest
                }
                val res = response as TLRPC.TL_channels_channelParticipants
                MessagesController.getInstance().putUsers(res.users, false)
                observer.onNext(res.users)
                observer.onComplete()
            }
            val classGuid = ConnectionsManager.getInstance().generateClassGuid()
            ConnectionsManager.getInstance().bindRequestToGuid(reqId, classGuid)
        }

}





