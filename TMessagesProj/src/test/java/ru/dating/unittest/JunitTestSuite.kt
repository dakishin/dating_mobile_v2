package ru.dating.unittest

import org.junit.runner.RunWith
import org.junit.runners.Suite
import ru.dating.unittest.tests.LocationInteractorTest
import ru.dating.unittest.tests.NearMePresenterTest
import ru.dating.unittest.tests.RegisterInteractorTest
import ru.dating.unittest.tests.SaveLocationInteractorTest
import ru.dating.unittest.tests.billing.BillingClientProviderTest
import ru.dating.unittest.tests.billing.BuyInteractorTest
import ru.dating.unittest.tests.billing.ConsumeInteractorTest
import ru.dating.unittest.tests.billing.GetPurchasesInteractorTest


/**
 *   Created by dakishin@gmail.com
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(SaveLocationInteractorTest::class, LocationInteractorTest::class, NearMePresenterTest::class,
    RegisterInteractorTest::class, BillingClientProviderTest::class, BuyInteractorTest::class, GetPurchasesInteractorTest::class,
    ConsumeInteractorTest::class)
class JunitTestSuite