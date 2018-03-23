package com.dating.modules

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.dating.billing.BillingClientProvider
import com.dating.billing.BuyInteractorProduction
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */

@Module
class PurchaseModuleProduction : PurchaseModule {

    @Provides
    @Singleton
    override fun buyInteractorLocator(): BuyInteractorLocator = BuyInteractorLocatorProduction()
}


interface PurchaseModule {
    fun buyInteractorLocator(): BuyInteractorLocator
}

interface BuyInteractor {
    fun buy(sku: String): Observable<PurchaseEvent>
}

interface BuyInteractorLocator {
    fun provideBuyInteractor(activity: Activity, bag: CompositeDisposable): BuyInteractor
}

open class DatingPurchase(val sku: String, val purchaseToken: String, val orderId: String)
data class PurchaseEvent constructor(val responseCode: Int, val purchases: List<DatingPurchase> = listOf())


private class BuyInteractorLocatorProduction : BuyInteractorLocator {

    override fun provideBuyInteractor(activity: Activity, bag: CompositeDisposable): BuyInteractor =
        BuyInteractorProduction(activity, BillingClientProvider(activity, bag,
            {
                BillingClient.newBuilder(activity.applicationContext)
                    .setListener(it ?: PurchasesUpdatedListener { responseCode, purchases -> })
                    .build()
            }
        ), PublishSubject.create(), Schedulers.computation())

}
