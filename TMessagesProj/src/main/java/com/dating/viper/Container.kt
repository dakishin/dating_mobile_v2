package com.dating.viper

import com.arellomobile.mvp.MvpPresenter


abstract class Container<M, V : View<M>> : MvpPresenter<V>() {
    abstract var viewModel: M
    var presenter: Presenter<M, V, *, *>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        presenter?.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }
}