package ru.dating.unittest.tests

import android.location.Location
import com.dating.api.DatingApi
import com.dating.interactors.GeoPreferences
import com.dating.interactors.LocationInteractor
import com.dating.interactors.ProfilePreferences
import com.dating.interactors.SaveLocationInteractor
import com.dating.util.Optional
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import ru.dating.unittest.base.BaseRobotoTest
import java.util.concurrent.TimeUnit

/**
 *   Created by dakishin@gmail.com
 */
class SaveLocationInteractorTest : BaseRobotoTest() {

    @Mock
    lateinit var geoPreferences: GeoPreferences

    @Mock
    lateinit var api: DatingApi

    @Mock
    lateinit var locationInteractor: LocationInteractor

    @Mock
    lateinit var profilePreferences: ProfilePreferences

    @Mock
    lateinit var location: Location

    lateinit var testScheduler: TestScheduler


    @Before
    fun before() {
        testScheduler = TestScheduler()
    }

    @Test
    fun should_test_send_data() {
        Mockito.`when`(api.getCityByLocation(anyDouble(), anyDouble())).thenReturn("Moscow")
        Mockito.`when`(profilePreferences.getTelegramId()).thenReturn(1)
        Mockito.`when`(location.latitude).thenReturn(2.0)
        Mockito.`when`(location.longitude).thenReturn(3.0)
        Mockito.`when`(locationInteractor.getLocation()).thenReturn(Observable.just(Optional(location)))


        SaveLocationInteractor(geoPreferences, api, locationInteractor, profilePreferences, testScheduler, PublishSubject.create())
            .saveLocation()
            .test()
            .assertNoErrors()
            .assertComplete()


        verify(api).sendGeoData(1, 2.0, 3.0, "Moscow")

    }

    @Test
    fun should_test_empty_id() {
        Mockito.`when`(api.getCityByLocation(anyDouble(), anyDouble())).thenReturn("Moscow")
        Mockito.`when`(profilePreferences.getTelegramId()).thenReturn(null)
        Mockito.`when`(location.latitude).thenReturn(2.0)
        Mockito.`when`(location.longitude).thenReturn(3.0)
        Mockito.`when`(locationInteractor.getLocation()).thenReturn(Observable.just(Optional(location)))


        SaveLocationInteractor( geoPreferences, api, locationInteractor, profilePreferences, testScheduler, PublishSubject.create())
            .saveLocation()
            .test()
            .assertNoErrors()
            .assertComplete()


        verify(api, never()).sendGeoData(1, 2.0, 3.0, "Moscow")

    }


    @Test
    fun should_test_empty_location() {
        Mockito.`when`(api.getCityByLocation(anyDouble(), anyDouble())).thenReturn("Moscow")
        Mockito.`when`(profilePreferences.getTelegramId()).thenReturn(1)
        Mockito.`when`(location.latitude).thenReturn(2.0)
        Mockito.`when`(location.longitude).thenReturn(3.0)
        Mockito.`when`(locationInteractor.getLocation()).thenReturn(Observable.just(Optional()))


        SaveLocationInteractor( geoPreferences, api, locationInteractor, profilePreferences, testScheduler, PublishSubject.create())
            .saveLocation()
            .test()
            .assertNoErrors()
            .assertComplete()


        verify(api, never()).sendGeoData(1, 2.0, 3.0, "Moscow")

    }


    @Test
    fun should_test_error_decode_location() {
        Mockito.`when`(api.getCityByLocation(anyDouble(), anyDouble())).thenThrow(RuntimeException("fake"))
        Mockito.`when`(profilePreferences.getTelegramId()).thenReturn(1)
        Mockito.`when`(location.latitude).thenReturn(2.0)
        Mockito.`when`(location.longitude).thenReturn(3.0)
        Mockito.`when`(locationInteractor.getLocation()).thenReturn(Observable.just(Optional(location)))


        SaveLocationInteractor( geoPreferences, api, locationInteractor, profilePreferences, testScheduler, PublishSubject.create())
            .saveLocation()
            .test()
            .assertNoErrors()
            .assertComplete()


        verify(api, never()).sendGeoData(anyInt(), anyDouble(), anyDouble(), anyString())

    }


    @Test
    fun should_test_send_if_needed() {
        Mockito.`when`(api.getCityByLocation(anyDouble(), anyDouble())).thenReturn("Moscow")
        Mockito.`when`(profilePreferences.getTelegramId()).thenReturn(1)
        Mockito.`when`(location.latitude).thenReturn(2.0)
        Mockito.`when`(location.longitude).thenReturn(3.0)
        Mockito.`when`(locationInteractor.getLocation()).thenReturn(Observable.just(Optional(location)))


        val geoDataSender = SaveLocationInteractor( geoPreferences, api, locationInteractor, profilePreferences, testScheduler, PublishSubject.create())

        geoDataSender
            .saveLocationIfNeeded()
            .test()
            .assertNoErrors()
            .assertComplete()
        verify(api, times(1)).sendGeoData(anyInt(), anyDouble(), anyDouble(), anyString())



        testScheduler.advanceTimeBy(20, TimeUnit.HOURS)
        geoDataSender
            .saveLocationIfNeeded()
            .test()
            .assertNoErrors()
            .assertComplete()
        verify(api, times(1)).sendGeoData(anyInt(), anyDouble(), anyDouble(), anyString())



        testScheduler.advanceTimeBy(5, TimeUnit.HOURS)
        geoDataSender
            .saveLocationIfNeeded()
            .test()
            .assertNoErrors()
            .assertComplete()
        verify(api, times(2)).sendGeoData(anyInt(), anyDouble(), anyDouble(), anyString())

    }

}