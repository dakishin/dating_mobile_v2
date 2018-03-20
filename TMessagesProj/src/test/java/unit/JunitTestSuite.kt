package unit

import org.junit.runner.RunWith
import org.junit.runners.Suite
import unit.tests.LocationInteractorTest
import unit.tests.NearMePresenterTest
import unit.tests.RegisterInteractorTest
import unit.tests.SaveLocationInteractorTest
import unit.tests.billing.BillingClientProviderTest
import unit.tests.billing.BuyInteractorTest
import unit.tests.billing.ConsumeInteractorTest
import unit.tests.billing.GetPurchasesInteractorTest


/**
 *   Created by dakishin@gmail.com
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(SaveLocationInteractorTest::class, LocationInteractorTest::class, NearMePresenterTest::class,
    RegisterInteractorTest::class, BillingClientProviderTest::class, BuyInteractorTest::class, GetPurchasesInteractorTest::class,
    ConsumeInteractorTest::class)
class JunitTestSuite