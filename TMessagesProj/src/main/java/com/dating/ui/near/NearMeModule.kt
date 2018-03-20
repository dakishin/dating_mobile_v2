package com.dating.ui.near

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.dating.api.DatingApi
import com.dating.api.TelegramApi
import com.dating.billing.BillingClientProvider
import com.dating.billing.BuyInteractor
import com.dating.billing.GetPurchasesInteractor
import com.dating.interactors.PermissionInteractor
import com.dating.interactors.ProfilePreferences
import com.dating.interactors.SaveLocationInteractor
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
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
    fun providePurchaseInteractor() =
        GetPurchasesInteractor(activity, BillingClientProvider(activity, bag, {
            BillingClient.newBuilder(activity)
                .setListener(it ?: PurchasesUpdatedListener { responseCode, purchases -> })
                .build()
        }), Schedulers.computation())

    @Provides
    @NearMeScope
    fun provideBuyInteractor() = BuyInteractor(activity, BillingClientProvider(activity, bag,
        {
            BillingClient.newBuilder(activity)
                .setListener(it ?: PurchasesUpdatedListener { responseCode, purchases -> })
                .build()
        }
    ), PublishSubject.create(), Schedulers.computation())


    @Provides
    @NearMeScope
    fun provideNearMeListInteractor(datingApi: DatingApi, telegramApi: TelegramApi, profilePreferences: ProfilePreferences) = NearMeListInteractor(telegramApi, datingApi, profilePreferences)


    @Provides
    @NearMeScope
    fun providePresenter(router: NearMeRouter, geoModule: SaveLocationInteractor, purchaseInteractor: GetPurchasesInteractor,
                         permissionInteractor: PermissionInteractor,
                         preferences: ProfilePreferences, api: DatingApi, nearMeListInteractor: NearMeListInteractor, buyInteractor: BuyInteractor) =
        NearMePresenter(router, bag, geoModule, purchaseInteractor, buyInteractor, preferences, activity, api,
            nearMeListInteractor, permissionInteractor, container)

    @Provides
    @NearMeScope
    fun provideActivity(): Activity = activity

}