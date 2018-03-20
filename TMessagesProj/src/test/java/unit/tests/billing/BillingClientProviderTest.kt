package unit.tests.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.dating.billing.BillingClientProvider
import com.dating.ui.treba.TrebaActivity
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.disposables.CompositeDisposable
import org.amshove.kluent.any
import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.times
import unit.base.BaseRobotoTest

/**
 *   Created by dakishin@gmail.com
 */
class BillingClientProviderTest : BaseRobotoTest() {

    @Mock
    lateinit var activity: TrebaActivity

    lateinit var billingClient: BillingClient

    private lateinit var bag: CompositeDisposable

    private lateinit var billingClientProvider: BillingClientProvider

    override fun setUp() {
        super.setUp()
        bag = CompositeDisposable()
        billingClient = mock(BillingClient::class)
        billingClientProvider = BillingClientProvider(activity, bag, { billingClient })
    }

    @Test
    fun shouldProvideClient() {
        val client = billingClientProvider.provide(PurchasesUpdatedListener { _, _ -> })

        verify(billingClient, times(1)).startConnection(any())

        billingClient shouldEqual client
        bag.size() shouldEqual 1
    }


    @Test
    fun shouldProvideClientWithNullListener() {
        val client = billingClientProvider.provide()

        verify(billingClient, times(1)).startConnection(any())

        billingClient shouldEqual client
        bag.size() shouldEqual 1
    }


    @Test
    fun shouldDisconnect() {
        val client = billingClientProvider.provide()

        whenever(billingClient.isReady).thenReturn(true)

        verify(billingClient, times(1)).startConnection(any())

        billingClient shouldEqual client
        bag.size() shouldEqual 1

        bag.dispose()

        verify(billingClient, times(1)).endConnection()

    }


}