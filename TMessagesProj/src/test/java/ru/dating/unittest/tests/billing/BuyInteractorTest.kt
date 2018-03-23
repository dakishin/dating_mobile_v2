package ru.dating.unittest.tests.billing

import com.android.billingclient.api.BillingClient
import com.dating.billing.BillingClientProvider
import com.dating.billing.BuyInteractorProduction
import com.dating.modules.DatingPurchase
import com.dating.modules.PurchaseEvent
import com.dating.ui.treba.TrebaActivity
import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.mockito.Mock
import ru.dating.unittest.base.BaseRobotoTest
import java.util.concurrent.TimeUnit

/**
 *   Created by dakishin@gmail.com
 */
class BuyInteractorTest : BaseRobotoTest() {

    @Mock
    lateinit var activity: TrebaActivity

    @Mock
    lateinit var billingClient: BillingClient

    @Mock
    lateinit var billingClientProvider: BillingClientProvider

    lateinit var buyInteractor: BuyInteractorProduction

    lateinit var testScheduler: TestScheduler

    val purchaseCreated = PublishSubject.create<PurchaseEvent>()

    override fun setUp() {
        super.setUp()
        testScheduler = TestScheduler()
        whenever(billingClientProvider.provide(any())).thenReturn(billingClient)
        doReturn(true).`when`(billingClient).isReady
        buyInteractor = BuyInteractorProduction(activity, billingClientProvider, purchaseCreated, testScheduler)
    }

    @Test
    fun testBuySuccess() {
        whenever(billingClient.launchBillingFlow(any(), any())).doAnswer<Int> {
            BillingClient.BillingResponse.OK
        }
        val test = buyInteractor
            .buy("sku")
            .test()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        purchaseCreated.onNext(PurchaseEvent(BillingClient.BillingResponse.OK, listOf(mock<DatingPurchase>())))
        test
            .assertValue { it.purchases.isNotEmpty() }
    }

    @Test
    fun testBuyException() {
        whenever(billingClient.launchBillingFlow(any(), any())).thenThrow(RuntimeException("fake"))
        val test = buyInteractor
            .buy("sku")
            .test()
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        test
            .assertValue { it.purchases.isEmpty() }
    }

    @Test
    fun testErrorResponse() {
        whenever(billingClient.launchBillingFlow(any(), any())).doAnswer<Int> {
            BillingClient.BillingResponse.BILLING_UNAVAILABLE
        }
        val test = buyInteractor
            .buy("sku")
            .test()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        test
            .assertValue { it.purchases.isEmpty() }
    }


    @Test
    fun testSuccessAfterError() {
        whenever(billingClient.launchBillingFlow(any(), any())).doAnswer<Int> {
            BillingClient.BillingResponse.BILLING_UNAVAILABLE
        }
        var test = buyInteractor
            .buy("sku")
            .test()
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)


        test.assertValue { it.purchases.isEmpty() }

        whenever(billingClient.launchBillingFlow(any(), any())).doAnswer<Int> {
            BillingClient.BillingResponse.OK
        }

        test = buyInteractor
            .buy("sku")
            .test()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        purchaseCreated.onNext(PurchaseEvent(BillingClient.BillingResponse.OK, listOf(mock<DatingPurchase>())))

        test.assertValue { it.purchases.isNotEmpty() }


    }
}