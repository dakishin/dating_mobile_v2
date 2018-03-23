package com.dating.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.dating.modules.BuyInteractor
import com.dating.modules.DatingPurchase
import com.dating.modules.PurchaseEvent
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 *   Created by dakishin@gmail.com
 */
open class BuyInteractorProduction constructor(val activity: Activity, billingClientProvider: BillingClientProvider,
                                               val purchaseCreated: PublishSubject<PurchaseEvent>,
                                               private val timeoutScheduler: Scheduler) : PurchasesUpdatedListener, BuyInteractor {


    private val TAG = BuyInteractorProduction::class.java.name
    private var billingClient: BillingClient = billingClientProvider.provide(this)


    override fun buy(sku: String): Observable<PurchaseEvent> =
        Observable
            .intervalRange(1, 10, 0, 1, TimeUnit.SECONDS, timeoutScheduler)
            .skipWhile { !billingClient.isReady }
            .take(1)
            .map {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSku(sku)
                    .setType(BillingClient.SkuType.INAPP)
                    .build()
                billingClient.launchBillingFlow(activity, flowParams)
            }
            .flatMap { code ->
                if (code != BillingClient.BillingResponse.OK) {
                    Observable.just(PurchaseEvent(code))
                } else {
                    purchaseCreated
                }
            }
            .doOnError {
                Log.e(TAG, it.message, it)
            }
            .onErrorReturnItem(PurchaseEvent(-1))


    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        val datingPurchases = mutableListOf<DatingPurchase>()
        purchases?.forEach {
            datingPurchases.add(DatingPurchase(it.sku, it.purchaseToken,it.orderId))
        }
        purchaseCreated.onNext(PurchaseEvent(responseCode, datingPurchases))
    }


}