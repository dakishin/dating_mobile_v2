package unit.tests

import com.dating.api.DatingApi
import com.dating.billing.BuyInteractor
import com.dating.billing.GetPurchasesInteractor
import com.dating.interactors.*
import com.dating.ui.near.*
import com.dating.ui.near.view.`NearMeView$$State`
import com.dating.ui.treba.InRoute
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.telegram.ui.LaunchActivity
import unit.base.*
import unit.base.TestRouter


/**
 *   Created by dakishin@gmail.com
 */

class NearMePresenterTest : BaseRobotoTest() {

    val router = TestRouter<ToRoute, InRoute>(CompositeDisposable())

    val bag = CompositeDisposable()

    lateinit var saveLocationInteractor: SaveLocationInteractor


    @Mock
    lateinit var buyInteractor: BuyInteractor


    @Mock
    lateinit var purchaceInteractor: GetPurchasesInteractor
    @Mock
    lateinit var profilePreferences: ProfilePreferences
    @Mock
    lateinit var activity: LaunchActivity
    @Mock
    lateinit var api: DatingApi
    @Mock
    lateinit var nearMeListInteractor: NearMeListInteractor

    @Mock
    lateinit var permissionInteractor: PermissionInteractor

    @Mock
    lateinit var viewState: `NearMeView$$State`

    @Mock
    lateinit var geoPreferences: GeoPreferences

    @Mock
    lateinit var locationInteractor: LocationInteractor


    lateinit var testScheduler: TestScheduler

    val container = NearMeContainer()

    private lateinit var presenter: NearMePresenter

    @Before
    override fun setUp() {
        super.setUp()
        container.setViewState(viewState)
        testScheduler = TestScheduler()

        saveLocationInteractor = spy(SaveLocationInteractor(geoPreferences, api,
            locationInteractor, profilePreferences, testScheduler, PublishSubject.create()))

        presenter = NearMePresenter(router, bag, saveLocationInteractor, purchaceInteractor,buyInteractor,
            profilePreferences, activity, api, nearMeListInteractor, permissionInteractor, container)


    }

    @Test
    fun test_request_location_success() {
        val sendGeodataCompletable = Observable.just(Unit)
        doReturn(sendGeodataCompletable).`when`(saveLocationInteractor).saveLocation()
        doReturn(true).`when`(saveLocationInteractor).hasLocation()

        whenever(permissionInteractor.isGeoPermissionGranted()).thenReturn(true)

        val sendGeoDataSubscriber = sendGeodataCompletable.test()
        val routeSubscription = router.toRoute.test()
        presenter.action.onNext(Action.CLICK_GET_LOCATION())


        sendGeoDataSubscriber.assertComplete()

        argumentCaptor<NearMeViewModel>().apply {
            verify(viewState, Mockito.times(2)).renderViewModel(capture())
            Assert.assertEquals(`1`().isLoading, true)
            Assert.assertEquals(`2`().isLoading, false)
        }


        routeSubscription.assertValue { it is ToRoute.NEAR_ME_LIST }

    }


    @Test
    fun test_request_location_fail() {
        doReturn(Observable.just(Unit)).`when`(saveLocationInteractor).saveLocation()
        doReturn(false).`when`(saveLocationInteractor).hasLocation()

        whenever(permissionInteractor.isGeoPermissionGranted()).thenReturn(true)

        val routeSubscription = router.toRoute.test()
        presenter.action.onNext(Action.CLICK_GET_LOCATION())

        argumentCaptor<NearMeViewModel>().apply {
            verify(viewState, Mockito.times(2)).renderViewModel(capture())
            Assert.assertEquals(`1`().isLoading, true)
            Assert.assertEquals(`2`().isLoading, false)
        }

        routeSubscription.assertValue { it is ToRoute.NO_LOCATION }

    }


}