package com.dating.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.BillingClient
import io.reactivex.Observable
import io.reactivex.Scheduler
import java.util.concurrent.TimeUnit

/**
 *   Created by dakishin@gmail.com
 */
class ConsumeInteractor constructor(val activity: Activity,
                                    billingClientProvider: BillingClientProvider,
                                    private val timeoutScheduler: Scheduler) {

    private val TAG = GetPurchasesInteractor::class.java.name
    private val billingClient: BillingClient = billingClientProvider.provide()


    fun consumeByPurchaseToken(purchaseToken: String): Observable<Int> =
        Observable
            .intervalRange(1, 10, 0, 1, TimeUnit.SECONDS, timeoutScheduler)
            .skipWhile { !billingClient.isReady }
            .take(1)
            .flatMap {
                Observable.create<Int> { subject ->
                    Log.d(TAG, "start consuming token:$purchaseToken")
                    billingClient.consumeAsync(purchaseToken) { responseCode, purchaseToken ->
                        Log.d(TAG, "end consuming code:$responseCode token:$purchaseToken")
                        subject.onNext(responseCode)
                        subject.onComplete()
                    }
                }
            }
            .doOnError {
                Log.d(TAG, it.message, it)
            }
            .onErrorReturnItem(-1)

}