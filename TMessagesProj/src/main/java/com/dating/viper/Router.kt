package com.dating.viper

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject


interface Router<I, O> {
    val bag: CompositeDisposable
    val route: PublishSubject<I>
    val output: PublishSubject<O>
}

fun <T : DisposableObserver<out Any?>, I, O> Router<I, O>.bind(observer: T) {
    bag.add(observer)
}