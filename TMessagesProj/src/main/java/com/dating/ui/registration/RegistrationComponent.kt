package com.dating.ui.registration

import dagger.Subcomponent

/**
 *   Created by dakishin@gmail.com
 */
@RegistrationScope
@Subcomponent(modules = arrayOf(RegistrationModule::class))
interface RegistrationComponent {
    fun presenter(): RegistrationPresenter
}