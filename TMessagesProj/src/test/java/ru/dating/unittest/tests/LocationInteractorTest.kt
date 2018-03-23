package ru.dating.unittest.tests

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.NETWORK_PROVIDER
import com.dating.interactors.LocationInteractor
import com.dating.interactors.PermissionInteractor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.robolectric.Shadows.shadowOf
import ru.dating.unittest.base.BaseRobotoTest


/**
 *   Created by dakishin@gmail.com
 */

class LocationInteractorTest : BaseRobotoTest() {

    lateinit var permissionInteractor: PermissionInteractor

    @Before
    override fun setUp() {
        super.setUp()
        permissionInteractor = spy(PermissionInteractor(context))
    }

    @Test
    fun should_get_no_location() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val testScheduler = TestScheduler()

        doReturn(true).whenever(permissionInteractor).isGeoPermissionGranted()

        val locationInteractor = LocationInteractor(context, testScheduler, locationManager, permissionInteractor)

        val subscriber = locationInteractor.getLocation().test()

        subscriber
            .assertComplete()
            .assertNoErrors()
            .assertValue { it.empty() }
    }

    @Test
    fun should_get_last_known_location() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val shadowLocationManager = shadowOf(locationManager)
        doReturn(true).whenever(permissionInteractor).isGeoPermissionGranted()

        shadowLocationManager.simulateLocation(Location(NETWORK_PROVIDER))


        Assert.assertNotNull(locationManager.getLastKnownLocation(NETWORK_PROVIDER))

        val testScheduler = TestScheduler()
        LocationInteractor(context, testScheduler, locationManager, permissionInteractor)
            .getLocation()
            .test()
            .assertValue { it.notEmpty() }
            .assertComplete()


    }


    @Test
    fun should_get_location_via_update() {
        val locationManager = spy(context.getSystemService(Context.LOCATION_SERVICE)) as LocationManager
        val shadowLocationManager = shadowOf(locationManager)
        doReturn(true).whenever(permissionInteractor).isGeoPermissionGranted()
        doReturn(NETWORK_PROVIDER).`when`(locationManager).getBestProvider(Mockito.any(Criteria::class.java), eq(true))


        assertNull(locationManager.getLastKnownLocation(NETWORK_PROVIDER))

        val testScheduler = TestScheduler()
        val subscriber = LocationInteractor(context, testScheduler, locationManager, permissionInteractor)
            .getLocation()
            .test()
            .assertNotComplete()

        shadowLocationManager.simulateLocation(Location(NETWORK_PROVIDER))
        subscriber
            .assertValue { it.notEmpty() }
            .assertComplete()
    }


    @Test
    fun should_get_while_not_granted_permission() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val shadowLocationManager = shadowOf(locationManager)

        doReturn(false).whenever(permissionInteractor).isGeoPermissionGranted()

        shadowLocationManager.simulateLocation(Location(NETWORK_PROVIDER))
        assertNotNull(locationManager.getLastKnownLocation(NETWORK_PROVIDER))

        val testScheduler = TestScheduler()
        LocationInteractor(context, testScheduler, locationManager, permissionInteractor)
            .getLocation()
            .test()
            .assertValue { it.empty() }
            .assertComplete()

    }


}