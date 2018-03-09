package com.dating.ui.registration

import android.app.Activity
import com.dating.api.DatingApi
import com.dating.interactors.GeoDataSender
import com.dating.interactors.ProfilePreferences
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
    fun provideRouter(geoModule: GeoDataSender): RegistrationRouter
        = RegistrationRouter(activity, bag, geoModule)

    @Provides
    @RegistrationScope
    fun providePresenter(router: RegistrationRouter, geoModule: GeoDataSender,
                         preferences: ProfilePreferences, api: DatingApi)
        = RegistrationPresenter(router, bag, preferences, activity, api)

    @Provides
    @RegistrationScope
    fun provideActivity(): Activity = activity


}