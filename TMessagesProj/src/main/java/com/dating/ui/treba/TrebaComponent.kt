package com.dating.ui.treba

import dagger.Subcomponent

/**
 *   Created by dakishin@gmail.com
 */
@TrebaScope
@Subcomponent(modules = arrayOf(TrebaModule::class))
interface TrebaComponent {
    fun presenter(): TrebaPresenter
}