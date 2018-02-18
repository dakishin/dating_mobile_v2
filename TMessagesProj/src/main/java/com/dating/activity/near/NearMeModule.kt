package com.dating.activity.near

import android.app.Activity
import com.dating.api.DatingApi
import com.dating.api.TelegramApi
import com.dating.modules.GeoModule
import com.dating.modules.ProfilePreferences
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import org.telegram.ui.LaunchActivity

/**
 *   Created by dakishin@gmail.com
 */
@Module
class NearMeModule(val activity: LaunchActivity) {

    val bag = CompositeDisposable()

    @Provides
    @NearMeScope
    fun provideRouter(geoModule: GeoModule): NearMeRouter
        = NearMeRouter(activity, bag, geoModule)


    @Provides
    @NearMeScope
    fun providePurchaseInteractor() = PurchaseInteractor(activity, bag)

    @Provides
    @NearMeScope
    fun provideNearMeListInteractor(datingApi: DatingApi, telegramApi: TelegramApi,profilePreferences: ProfilePreferences)
        = NearMeListInteractor(telegramApi, datingApi,profilePreferences)


    @Provides
    @NearMeScope
    fun providePresenter(router: NearMeRouter, geoModule: GeoModule, purchaseModule: PurchaseInteractor,
                         preferences: ProfilePreferences, api: DatingApi, nearMeListInteractor: NearMeListInteractor)
        = NearMePresenter(router, bag, geoModule, purchaseModule, preferences, activity, api, nearMeListInteractor)

    @Provides
    @NearMeScope
    fun provideActivity(): Activity = activity


//    @Provides
//    @NearMeScope
//    fun provideDelegate(): MvpDelegate<Any> = delegate


}