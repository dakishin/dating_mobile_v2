package com.dating.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.PurchasesUpdatedListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *   Created by dakishin@gmail.com
 */
open class BillingClientProvider constructor(val activity: Activity, val bag: CompositeDisposable,
                                             private val createBillingClient: (listener: PurchasesUpdatedListener?) -> BillingClient) {

    private lateinit var mBillingClient: BillingClient

    private val TAG = BillingClientProvider::class.java.name

    private val destroyInAppService = object : Disposable {
        var disposed = false;

        override fun dispose() {
            try {
                if (mBillingClient.isReady) {
                    mBillingClient.endConnection()
                }
                Log.d(TAG, "billing disconnected")
            } catch (e: Exception) {

            }
            disposed = true
        }

        override fun isDisposed(): Boolean {
            return disposed
        }
    }


    open fun provide(listener: PurchasesUpdatedListener? = null): BillingClient {
        mBillingClient = createBillingClient(listener)

        mBillingClient.startConnection(object : BillingClientStateListener {
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
        return mBillingClient
    }


}