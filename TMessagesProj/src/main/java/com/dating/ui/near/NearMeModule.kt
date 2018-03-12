package com.dating.ui.near

import android.app.Activity
import com.dating.api.DatingApi
import com.dating.api.TelegramApi
import com.dating.interactors.PermissionInteractor
import com.dating.interactors.ProfilePreferences
import com.dating.interactors.SaveLocationInteractor
import com.dating.ui.base.PurchaseInteractor
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import org.telegram.ui.LaunchActivity

/**
 *   Created by dakishin@gmail.com
 */
@Module
class NearMeModule(val activity: LaunchActivity, val container: NearMeContainer) {

    val bag = CompositeDisposable()

    @Provides
    @NearMeScope
    fun provideRouter(geoModule: SaveLocationInteractor): NearMeRouter = NearMeRouter(activity, bag, geoModule)


    @Provides
    @NearMeScope
    fun providePurchaseInteractor() = PurchaseInteractor(activity, bag)

    @Provides
    @NearMeScope
    fun provideNearMeListInteractor(datingApi: DatingApi, telegramApi: TelegramApi, profilePreferences: ProfilePreferences) = NearMeListInteractor(telegramApi, datingApi, profilePreferences)


    @Provides
    @NearMeScope
    fun providePresenter(router: NearMeRouter, geoModule: SaveLocationInteractor, purchaseModule: PurchaseInteractor,
                         permissionInteractor: PermissionInteractor,
                         preferences: ProfilePreferences, api: DatingApi, nearMeListInteractor: NearMeListInteractor) =
        NearMePresenter(router, bag, geoModule, purchaseModule, preferences, activity, api,
            nearMeListInteractor, permissionInteractor, container)

    @Provides
    @NearMeScope
    fun provideActivity(): Activity = activity

}