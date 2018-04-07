package com.dating.ui.base

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.dating.api.ClientNeedUpdateException
import com.dating.interactors.ProfilePreferences
import com.dating.viper.Observer
import io.reactivex.subjects.PublishSubject

/**
 *   Created by dakishin@gmail.com
 */
sealed class ApiErrors {
    class UPDATE_NEEDED : ApiErrors()
    class INTERNAL_ERROR : ApiErrors()
}

interface ApiErrorsPresenter {
    val apiErrors: PublishSubject<ApiErrors>
}

open class ApiObserver<T>(val presenter: ApiErrorsPresenter, val preferences: ProfilePreferences) : Observer<T>() {

    var isErrorHandled = false

    override fun onError(e: Throwable) {
        super.onError(e)
        if (isErrorHandled) {
            return
        }

        isErrorHandled = true
        if (e is ClientNeedUpdateException) {
            Log.e("UPDATE_TAG", "start update router")
            presenter.apiErrors.onNext(ApiErrors.UPDATE_NEEDED())
            return
        }


        Crashlytics.log("error. telegramId:${preferences.getTelegramId()}")
        Crashlytics.logException(e)
        presenter.apiErrors.onNext(ApiErrors.INTERNAL_ERROR())
    }


}