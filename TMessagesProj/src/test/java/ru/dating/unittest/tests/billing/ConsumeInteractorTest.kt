package ru.dating.unittest.tests.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ConsumeResponseListener
import com.dating.billing.BillingClientProvider
import com.dating.billing.ConsumeInteractor
import com.dating.ui.treba.TrebaActivity
import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import org.mockito.Mock
import ru.dating.unittest.base.BaseRobotoTest
import java.util.concurrent.TimeUnit

/**
 *   Created by dakishin@gmail.com
 */
class ConsumeInteractorTest : BaseRobotoTest() {


    @Mock
    lateinit var activity: TrebaActivity

    @Mock
    lateinit var billingClient: BillingClient


    @Mock
    lateinit var billingClientProvider: BillingClientProvider

    lateinit var consumeInteractor: ConsumeInteractor

    lateinit var testScheduler: TestScheduler

    override fun setUp() {
        super.setUp()
        testScheduler = TestScheduler()
        whenever(billingClientProvider.provide(anyOrNull())).thenReturn(billingClient)
        consumeInteractor = ConsumeInteractor(activity, billingClientProvider, testScheduler)
    }


    @Test
    fun shouldConsume() {
        doReturn(true).`when`(billingClient).isReady

        whenever(billingClient.consumeAsync(any(), any())).doAnswer<Unit> {
            val listener = it.arguments[1] as ConsumeResponseListener
            listener.onConsumeResponse(BillingClient.BillingResponse.OK, "xx")
        }

        val test = consumeInteractor
            .consumeByPurchaseToken("token")
            .test()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)

        test
            .assertComplete()
    }


    @Test
    fun shouldWaitInit() {
        doReturn(false).`when`(billingClient).isReady
        whenever(billingClient.consumeAsync(any(), any())).doAnswer<Unit> {
            val listener = it.arguments[1] as ConsumeResponseListener
            listener.onConsumeResponse(BillingClient.BillingResponse.OK, "xx")
        }

        val test = consumeInteractor
            .consumeByPurchaseToken("xxx")
            .test()

        test
            .assertNotComplete()

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test
            .assertNotComplete()

        doReturn(true).`when`(billingClient).isReady
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test
            .assertValue { it == BillingClient.BillingResponse.OK }
            .assertComplete()


    }


    @Test
    fun shouldTestInvalidResponse() {
        doReturn(true).`when`(billingClient).isReady
        whenever(billingClient.consumeAsync(any(), any())).doAnswer<Unit> {
            val listener = it.arguments[1] as ConsumeResponseListener
            listener.onConsumeResponse(-1, "xx")
        }

        val test = consumeInteractor
            .consumeByPurchaseToken("xxx")
            .test()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)

        test
            .assertValue { it == -1 }
            .assertComplete()
    }


    @Test
    fun shouldCatchException() {
        doReturn(true).`when`(billingClient).isReady
        whenever(billingClient.consumeAsync(any(), any())).thenThrow(RuntimeException("fake"))

        val test = consumeInteractor
            .consumeByPurchaseToken("xxx")
            .test()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test
            .assertValue { it == -1 }
            .assertComplete()
    }
}