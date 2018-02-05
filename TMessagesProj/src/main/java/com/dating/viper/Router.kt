package com.dating.viper

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject


interface Router<I, O, V> {
    val bag: CompositeDisposable
    val toRoute: PublishSubject<I>
    val inRoute: PublishSubject<O>
    val changeView: PublishSubject<V>

}

fun <T : DisposableObserver<out Any?>, I, O, V> Router<I, O, V>.bind(observer: T) {
    bag.add(observer)
}