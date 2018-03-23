package ru

import android.app.Activity
import com.dating.modules.*
import dagger.Component
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

/**
 *   Created by dakishin@gmail.com
 */

@Singleton
@Component(modules = arrayOf(AndroidModule::class, PurchaseModuleMock::class))
interface TestAppComponent : AppComponent

@Module
class PurchaseModuleMock : PurchaseModule {

    @Provides
    @Singleton
    override fun buyInteractorLocator(): BuyInteractorLocator = object : BuyInteractorLocator {
        override fun provideBuyInteractor(activity: Activity, bag: CompositeDisposable): BuyInteractor {
            return BuyInteractorMock()
        }

    }
}

class BuyInteractorMock : BuyInteractor {
    override fun buy(sku: String): Observable<PurchaseEvent> {

        val purchaseEvent = PurchaseEvent(1,
            listOf(DatingPurchase("treba_1_o_zdravii_v2", "xx","xx")))
        return Observable.just(purchaseEvent)
    }


}