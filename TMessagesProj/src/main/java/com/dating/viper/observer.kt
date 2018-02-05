package com.dating.viper

import android.util.Log
import com.dating.util.weak
import io.reactivex.observers.DisposableObserver

val TAG = "Daging obsevers"

abstract class Observer<T> : DisposableObserver<T>() {

    override fun onComplete() {}

    override fun onError(e: Throwable) {
        Log.e(TAG, e.message, e)
    }

    override fun onNext(next: T) {}
}


fun <T> NextObserver(doOnNext: (T) -> Unit): Observer<T> {
    return object : Observer<T>() {
        override fun onNext(next: T) {
            super.onNext(next)
            doOnNext(next)
        }
    }
}

fun <E, T> WeakNextObserver(target: E, doOnNext: (T) -> Unit): Observer<T> {
    return object : Observer<T>() {
        val weakTarget by weak(target)
        override fun onNext(next: T) {
            super.onNext(next)
            weakTarget?.apply {
                doOnNext(next)
            }
        }
    }
}

fun <T> CompleteObserver(doOnComplete: () -> Unit): Observer<T> {
    return object : Observer<T>() {
        override fun onComplete() {
            super.onComplete()
            doOnComplete()
        }
    }
}


fun <T> IgnoreErrorsObserver(doOnComplete: () -> Unit): Observer<T> {
    return object : Observer<T>() {
        override fun onComplete() {
            super.onComplete()
            doOnComplete()
        }

        override fun onNext(next: T) {
            super.onNext(next)
            doOnComplete()
        }

        override fun onError(e: Throwable) {
            super.onComplete()
            doOnComplete()
        }
    }
}




