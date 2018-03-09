package local

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.NETWORK_PROVIDER
import com.dating.interactors.LocationInteractor
import com.dating.interactors.PermissionInteractor
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.robolectric.Shadows.shadowOf
import java.util.concurrent.TimeUnit


/**
 *   Created by dakishin@gmail.com
 */

class LocationInteractorTest : BaseRobotoTest() {


    lateinit var permissionInteractorGranted: PermissionInteractor

    lateinit var permissionInteractorNotGranded: PermissionInteractor

    @Before
    override fun setUp() {
        super.setUp()
        permissionInteractorGranted = object : PermissionInteractor(context) {
            override fun isGeoPermissionGranted(): Boolean {
                return true
            }
        }

        permissionInteractorNotGranded = object : PermissionInteractor(context) {
            override fun isGeoPermissionGranted(): Boolean {
                return false
            }
        }

    }

    @Test
    fun should_get_no_location() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val testScheduler = TestScheduler()

        val locationInteractor = LocationInteractor(context, testScheduler, locationManager, permissionInteractorGranted)

        val subscriber = locationInteractor.getLocation().test()

        subscriber.assertNotComplete()
        subscriber.assertNoErrors()
        testScheduler.advanceTimeBy(15, TimeUnit.SECONDS)

        subscriber
            .assertComplete()
            .assertNoErrors()
            .assertValue { it.empty() }
    }

    @Test
    fun should_get_last_known_location() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val shadowLocationManager = shadowOf(locationManager)

        shadowLocationManager.simulateLocation(Location(NETWORK_PROVIDER))


        Assert.assertNotNull(locationManager.getLastKnownLocation(NETWORK_PROVIDER))

        val testScheduler = TestScheduler()
        LocationInteractor(context, testScheduler, locationManager, permissionInteractorGranted)
            .getLocation()
            .test()
            .assertValue { it.notEmpty() }
            .assertComplete()


    }


    @Test
    fun should_get_location_via_update() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val shadowLocationManager = shadowOf(locationManager)

        assertNull(locationManager.getLastKnownLocation(NETWORK_PROVIDER))

        val testScheduler = TestScheduler()
        val subscriber = LocationInteractor(context, testScheduler, locationManager, permissionInteractorGranted)
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

        shadowLocationManager.simulateLocation(Location(NETWORK_PROVIDER))
        assertNotNull(locationManager.getLastKnownLocation(NETWORK_PROVIDER))

        val testScheduler = TestScheduler()
        LocationInteractor(context, testScheduler, locationManager, permissionInteractorNotGranded)
            .getLocation()
            .test()
            .assertValue { it.empty() }
            .assertComplete()

    }

}