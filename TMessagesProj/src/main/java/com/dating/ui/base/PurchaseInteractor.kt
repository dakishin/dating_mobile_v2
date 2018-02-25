package com.dating.ui.base

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


/**
 *   Created by dakishin@gmail.com
 */
class PurchaseInteractor(val activity: Activity, bag: CompositeDisposable) : PurchasesUpdatedListener {

    val TAG = PurchaseInteractor::class.java.name

    var mBillingClient: BillingClient? = null

    private val destroyInAppService = object : Disposable {
        override fun dispose() {
            try {
                if (mBillingClient?.isReady == true) {
                    mBillingClient?.endConnection()
                }
                Log.d(TAG, "billing disconnected")
            } catch (e: Exception) {

            }
            mBillingClient = null

        }

        override fun isDisposed(): Boolean {
            return mBillingClient == null
        }
    }


    init {
        mBillingClient = BillingClient.newBuilder(activity)
            .setListener(this)
            .build()

        mBillingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {

                }
                Log.d(TAG, "billing connected. code " + billingResponseCode)
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "billing disconnected")
            }
        })


        bag.add(destroyInAppService)
    }

    val purchaseCreated = PublishSubject.create<PurchaseEvent>()

    fun buy(sku: String): Observable<PurchaseEvent> =
        Observable
            .fromCallable<Int> {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSku(sku)
                    .setType(SkuType.INAPP)
                    .build()
                mBillingClient?.launchBillingFlow(activity, flowParams)
            }
            .flatMap { code ->
                if (code != BillingClient.BillingResponse.OK) {
                    Observable.just(PurchaseEvent(code))
                } else {
                    purchaseCreated
                }
            }


    fun downloadPurchases(): Observable<List<Purchase>> =
        Observable
            .intervalRange(1, 10, 0, 1, TimeUnit.SECONDS)
            .skipWhile { !mBillingClient!!.isReady }
            .take(1)
            .flatMap {
                Observable.fromPublisher<List<Purchase>> { subscriber ->
                    mBillingClient!!.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP) { responseCode, purchasesList ->
                        if (responseCode == BillingClient.BillingResponse.OK) {
                            subscriber.onNext(purchasesList)
                            subscriber.onComplete()
                        } else {
                            subscriber.onError(RuntimeException("error while queryPurchaseHistoryAsync. code $responseCode"))
                        }
                    }
                }
            }


    fun consumeByPurchaseToken(purchaseToken: String): Observable<Int> =
        Observable.fromPublisher<Int> { subject ->
            Log.d(TAG, "start consuming token:$purchaseToken")
            mBillingClient?.consumeAsync(purchaseToken) { responseCode, purchaseToken ->
                Log.d(TAG, "end consuming code:$responseCode token:$purchaseToken")
                subject.onNext(responseCode)
                subject.onComplete()
            }
        }
            .doOnError {
                Log.d(TAG, "error consuming $purchaseToken")
            }


    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        purchaseCreated.onNext(PurchaseEvent(responseCode, purchases))
    }

    data class PurchaseEvent(val responseCode: Int, val purchases: MutableList<Purchase>? = null)

}