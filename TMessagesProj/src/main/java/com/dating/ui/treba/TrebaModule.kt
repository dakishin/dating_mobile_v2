package com.dating.ui.treba

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.dating.api.DatingApi
import com.dating.billing.BillingClientProvider
import com.dating.billing.ConsumeInteractor
import com.dating.billing.GetPurchasesInteractor
import com.dating.interactors.ProfilePreferences
import com.dating.interactors.SaveLocationInteractor
import com.dating.modules.BuyInteractor
import com.dating.modules.BuyInteractorLocator
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 *   Created by dakishin@gmail.com
 */
@Module
class TrebaModule(val activity: TrebaActivity) {

    val bag = CompositeDisposable()

    @Provides
    @TrebaScope
    fun provideRouter(geoModule: SaveLocationInteractor): TrebaRouter = TrebaRouter(activity, bag, geoModule)


    @Provides
    @TrebaScope
    fun providePurchaseInteractor() =
        GetPurchasesInteractor(activity, BillingClientProvider(activity, bag,
            {
                BillingClient.newBuilder(activity)
                    .setListener(it ?: PurchasesUpdatedListener { responseCode, purchases -> })
                    .build()
            }), Schedulers.computation())


    @Provides
    @TrebaScope
    fun provideBuyInteractor(buyInteractorLocator: BuyInteractorLocator) =
        buyInteractorLocator.provideBuyInteractor(activity, bag)


    @Provides
    @TrebaScope
    fun providePresenter(router: TrebaRouter, preferences: ProfilePreferences, api: DatingApi, buyInteractor: BuyInteractor,
                         purchaseInteractor: GetPurchasesInteractor, consumeInteractor: ConsumeInteractor) = TrebaPresenter(router, bag, preferences, activity,
        api, purchaseInteractor, buyInteractor, consumeInteractor)


    @Provides
    @TrebaScope
    fun provideConsumeInteractor() =
        ConsumeInteractor(activity, BillingClientProvider(activity, bag,
            {
                BillingClient.newBuilder(activity)
                    .setListener(it ?: PurchasesUpdatedListener { responseCode, purchases -> })
                    .build()
            }), Schedulers.computation())

    @Provides
    @TrebaScope
    fun provideActivity(): Activity = activity


}