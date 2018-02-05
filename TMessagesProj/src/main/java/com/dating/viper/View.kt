package com.dating.viper

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface View<Model> : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun renderViewModel(viewModel: Model)
}