package com.dating.activity.near

import com.dating.api.ClientNeedUpdateException
import com.dating.viper.Observer

/**
 *   Created by dakishin@gmail.com
 */
open class NearMeObserver<T>(val router: NearMeRouter) : Observer<T>() {


    override fun onError(e: Throwable) {
        super.onError(e)
        if (e is ClientNeedUpdateException) {
            router.inRoute.onNext(InRoute.UPDATE_NEEDED())
        }
    }

    override fun onComplete() {
        super.onComplete()
    }
}