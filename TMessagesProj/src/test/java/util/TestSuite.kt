package util

import local.LocationInteractorTest
import local.NearMePresenterTest
import local.RegisterInteractorTest
import local.SaveLocationInteractorTest
import org.junit.runner.RunWith
import org.junit.runners.Suite


/**
 *   Created by dakishin@gmail.com
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(SaveLocationInteractorTest::class, LocationInteractorTest::class, NearMePresenterTest::class,
    RegisterInteractorTest::class)
class JunitTestSuite