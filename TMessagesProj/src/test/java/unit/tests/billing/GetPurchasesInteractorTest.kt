package unit.tests.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryResponseListener
import com.dating.billing.BillingClientProvider
import com.dating.billing.GetPurchasesInteractor
import com.dating.ui.treba.TrebaActivity
import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import org.mockito.Mock
import unit.base.BaseRobotoTest
import java.util.concurrent.TimeUnit


/**
 *   Created by dakishin@gmail.com
 */
class GetPurchasesInteractorTest : BaseRobotoTest() {

    @Mock
    lateinit var activity: TrebaActivity

    @Mock
    lateinit var billingClient: BillingClient


    @Mock
    lateinit var billingClientProvider: BillingClientProvider

    lateinit var purchaseInteractor: GetPurchasesInteractor

    lateinit var testScheduler: TestScheduler

    override fun setUp() {
        super.setUp()
        testScheduler = TestScheduler()
        whenever(billingClientProvider.provide(anyOrNull())).thenReturn(billingClient)
        purchaseInteractor = GetPurchasesInteractor(activity, billingClientProvider, testScheduler)
    }

    @Test
    fun shouldDownloadPurchases() {
        doReturn(true).`when`(billingClient).isReady

        whenever(billingClient.queryPurchaseHistoryAsync(any(), any())).doAnswer<Unit> {
            val listener = it.arguments[1] as PurchaseHistoryResponseListener
            listener.onPurchaseHistoryResponse(BillingClient.BillingResponse.OK, listOf(mock<Purchase>()))
        }

        val test = purchaseInteractor
            .downloadPurchases()
            .test()
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        test
            .assertValue { it.isNotEmpty() }
            .assertComplete()
    }

    @Test
    fun shouldWaitInit() {
        doReturn(false).`when`(billingClient).isReady

        whenever(billingClient.queryPurchaseHistoryAsync(any(), any())).doAnswer<Unit> {
            val listener = it.arguments[1] as PurchaseHistoryResponseListener
            listener.onPurchaseHistoryResponse(BillingClient.BillingResponse.OK, listOf(mock<Purchase>()))
        }

        val test = purchaseInteractor
            .downloadPurchases()
            .test()

        test
            .assertNotComplete()

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test
            .assertNotComplete()

        doReturn(true).`when`(billingClient).isReady
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test
            .assertValue { it.isNotEmpty() }
            .assertComplete()


    }


    @Test
    fun shouldTestInvalidResponse() {
        doReturn(true).`when`(billingClient).isReady

        whenever(billingClient.queryPurchaseHistoryAsync(any(), any())).doAnswer<Unit> {
            val listener = it.arguments[1] as PurchaseHistoryResponseListener
            listener.onPurchaseHistoryResponse(-1, listOf(mock<Purchase>()))
        }
        val test = purchaseInteractor
            .downloadPurchases()
            .test()

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test
            .assertValue { it.isEmpty() }
            .assertComplete()
    }

    @Test
    fun shouldCatchException() {
        doReturn(true).`when`(billingClient).isReady

        whenever(billingClient.queryPurchaseHistoryAsync(any(), any())).thenThrow(RuntimeException("fake exception"))

        val test = purchaseInteractor
            .downloadPurchases()
            .test()

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test
            .assertValue { it.isEmpty() }
            .assertComplete()
    }


}