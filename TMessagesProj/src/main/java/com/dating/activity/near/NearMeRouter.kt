package com.dating.activity.near

import com.android.billingclient.api.BillingClient
import com.dating.activity.near.view.NearMeListFragment
import com.dating.activity.near.view.NearMeNoCoordFragment
import com.dating.modules.GeoModule
import com.dating.util.Utils
import com.dating.util.bindRouter
import com.dating.viper.NextObserver
import com.dating.viper.Router
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.telegram.ui.LaunchActivity


/**
 *   Created by dakishin@gmail.com
 */


sealed class ChangeViewRoute {
    class NEAR_ME_LIST : ChangeViewRoute()
    class NO_LOCATION : ChangeViewRoute()
    class BUY : ChangeViewRoute()
}

sealed class ToRoute {
    class LIVING_ROOM : ToRoute()
    class ASK_PRIEST_ROOM : ToRoute()
    class BOGOSLOV_ROOM : ToRoute()
    class CREATE_TREBA_ROOM : ToRoute()
    class UPDATE : ToRoute()
    class ASK_ADMIN : ToRoute()
}

sealed class InRoute {
    class UPDATE_NEEDED : InRoute()
    class PERMISSION_GRANTED : InRoute()
}


class NearMeRouter(val activity: LaunchActivity, override val bag: CompositeDisposable, val geoModule: GeoModule) : Router<ToRoute, InRoute, ChangeViewRoute> {

    override val toRoute: PublishSubject<ToRoute> = PublishSubject.create<ToRoute>()
    override val inRoute: PublishSubject<InRoute> = PublishSubject.create<InRoute>()
    override val changeView: PublishSubject<ChangeViewRoute> = PublishSubject.create<ChangeViewRoute>()

    val TAG = NearMeRouter::class.java.name


    private var mBillingClient: BillingClient? = null

    val destroyInAppService = object : Disposable {
        override fun dispose() {
            try {
                mBillingClient?.endConnection()
            } catch (e: Exception) {

            }
            mBillingClient = null

        }

        override fun isDisposed(): Boolean {
            return mBillingClient == null
        }
    }

    init {

        changeView
            .subscribeWith(NextObserver {
                when (it) {
                    is ChangeViewRoute.NO_LOCATION -> {
                        activity.presentFragment(NearMeNoCoordFragment.create(), true, true)
                    }

                    is ChangeViewRoute.NEAR_ME_LIST -> {
                        activity.presentFragment(NearMeListFragment.create(), true, true)
                    }

                }
            })
            .bindRouter(this)


        toRoute
            .subscribeWith(NextObserver {
                when (it) {
                    is ToRoute.LIVING_ROOM -> {

                    }
                    is ToRoute.UPDATE -> {
                        Utils.startUpdate(activity)
                    }
                    is ToRoute.ASK_ADMIN -> {
                        Utils.startAskAdmin(activity)
                    }
                }
            })
            .bindRouter(this)


    }


}