package com.dating.ui.registration

import android.app.Activity
import com.dating.api.DatingApi
import com.dating.modules.GeoModule
import com.dating.modules.ProfilePreferences
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

/**
 *   Created by dakishin@gmail.com
 */
@Module
class RegistrationModule(val activity: RegistrationActivity) {

    val bag = CompositeDisposable()


    @Provides
    @RegistrationScope
    fun provideRouter(geoModule: GeoModule): RegistrationRouter
        = RegistrationRouter(activity, bag, geoModule)

    @Provides
    @RegistrationScope
    fun providePresenter(router: RegistrationRouter, geoModule: GeoModule,
                         preferences: ProfilePreferences, api: DatingApi)
        = RegistrationPresenter(router, bag, preferences, activity, api)

    @Provides
    @RegistrationScope
    fun provideActivity(): Activity = activity


}