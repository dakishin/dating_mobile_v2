package unit.tests.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.dating.billing.BillingClientProvider
import com.dating.billing.BuyInteractor
import com.dating.ui.treba.TrebaActivity
import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.mockito.Mock
import unit.base.BaseRobotoTest
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

    lateinit var buyInteractor: BuyInteractor

    lateinit var testScheduler: TestScheduler

    val purchaseCreated = PublishSubject.create<BuyInteractor.PurchaseEvent>()

    override fun setUp() {
        super.setUp()
        testScheduler = TestScheduler()
        whenever(billingClientProvider.provide(any())).thenReturn(billingClient)
        doReturn(true).`when`(billingClient).isReady
        buyInteractor = BuyInteractor(activity, billingClientProvider, purchaseCreated, testScheduler)
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
        purchaseCreated.onNext(BuyInteractor.PurchaseEvent(BillingClient.BillingResponse.OK, listOf(mock<Purchase>())))
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
        purchaseCreated.onNext(BuyInteractor.PurchaseEvent(BillingClient.BillingResponse.OK, listOf(mock<Purchase>())))

        test.assertValue { it.purchases.isNotEmpty() }


    }
}