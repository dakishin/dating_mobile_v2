package com.dating.activity.near

import android.util.Log
import com.dating.api.ClientNeedUpdateException
import com.dating.viper.Observer

/**
 *   Created by dakishin@gmail.com
 */
open class NearMeObserver<T>(val presenter: NearMePresenter) : Observer<T>() {

    var isErrorHanded = false

    override fun onError(e: Throwable) {
        super.onError(e)
        if (isErrorHanded) {
            return
        }

        isErrorHanded = true
        if (e is ClientNeedUpdateException) {
            Log.e("UPDATE_TAG", "start update router")
            presenter.router.inRoute.onNext(InRoute.UPDATE_NEEDED())
            return
        }

        presenter.action.onNext(Action.UNEXPECTED_ERROR())
    }

    override fun onComplete() {
        super.onComplete()
    }
}