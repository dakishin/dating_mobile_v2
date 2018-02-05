package com.dating.viper

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver



interface Presenter<M, V : View<M>, A, O> {
    val bag: CompositeDisposable
    val action: Observable<A>
    val container: Container<M, V>
    val viewModel: M get() = container.viewModel

    @Deprecated("", ReplaceWith("NextObserver { }"))
    fun actionObserver(): Observer<A> = NextObserver { }

    @Deprecated("", ReplaceWith("NextObserver { }"))
    fun outputObserver(): Observer<O> = NextObserver { }

    fun start()

    fun onDestroy() {
        bag.clear()
    }

    fun renderVm(transform: M.() -> M) {
        updateVm(transform)
        container.viewState.renderViewModel(container.viewModel)
    }

    fun updateVm(transform: M.() -> M) {
        container.viewModel = transform(container.viewModel)
    }

}

fun <T : DisposableObserver<out Any?>, M, V : View<M>, A, O> Presenter<M, V, A, O>.bind(observer: T) {
    bag.add(observer)
}
