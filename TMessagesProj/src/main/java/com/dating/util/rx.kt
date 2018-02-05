package com.dating.util

import com.dating.viper.Presenter
import com.dating.viper.Router
import com.dating.viper.View
import com.dating.viper.bind
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 *   Created by dakishin@gmail.com
 */

fun <T> Observable<T>.ioScheduler(): Observable<T> =
    this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.uiScheduler(): Observable<T> =
    this.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())

fun <T, M, V : View<M>, A, O> DisposableObserver<T>.bindPresenter(observer: Presenter<M, V, A, O>):
    DisposableObserver<T> {
    observer.bind(this)
    return this
}

fun <T, I : Any, O : Any> DisposableObserver<T>.bindRouter(observer: Router<I, O>):
    DisposableObserver<T> {
    observer.bind(this)
    return this
}


class Optional<T>(val value: T? = null) {
    fun notEmpty() = value != null
    fun empty() = value == null
}

fun <T> toOptional(value: T?): Optional<T> = Optional(value)