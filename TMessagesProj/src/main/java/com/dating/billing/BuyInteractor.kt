package com.dating.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 *   Created by dakishin@gmail.com
 */
open class BuyInteractor constructor(val activity: Activity, billingClientProvider: BillingClientProvider,
                                val purchaseCreated: PublishSubject<PurchaseEvent>, private val timeoutScheduler: Scheduler) : PurchasesUpdatedListener {


    private val TAG = BuyInteractor::class.java.name
    private var billingClient: BillingClient = billingClientProvider.provide(this)


    fun buy(sku: String): Observable<PurchaseEvent> =
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
        purchaseCreated.onNext(PurchaseEvent(responseCode, purchases
            ?: listOf()))
    }

    data class PurchaseEvent(val responseCode: Int, val purchases: List<Purchase> = listOf())
}