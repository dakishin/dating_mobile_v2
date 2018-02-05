package com.dating.activity.near

import dagger.Subcomponent

/**
 *   Created by dakishin@gmail.com
 */
@NearMeScope
@Subcomponent(modules = arrayOf(NearMeModule::class))
interface NearMeComponent {
    fun presenter(): NearMePresenter
//    fun mvpDelegate(): MvpDelegate<Any>
}