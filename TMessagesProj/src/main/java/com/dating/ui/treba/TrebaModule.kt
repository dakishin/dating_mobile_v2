package com.dating.ui.treba

import android.app.Activity
import com.dating.ui.base.PurchaseInteractor
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
class TrebaModule(val activity: TrebaActivity) {

    val bag = CompositeDisposable()

    @Provides
    @TrebaScope
    fun provideRouter(geoModule: GeoDataSender): TrebaRouter
        = TrebaRouter(activity, bag, geoModule)


    @Provides
    @TrebaScope
    fun providePurchaseInteractor() = PurchaseInteractor(activity, bag)



    @Provides
    @TrebaScope
    fun providePresenter(router: TrebaRouter, geoModule: GeoDataSender,
                         preferences: ProfilePreferences, api: DatingApi, purchaseInteractor: PurchaseInteractor)
        = TrebaPresenter(router, bag, preferences, activity, api,purchaseInteractor)

    @Provides
    @TrebaScope
    fun provideActivity(): Activity = activity



}