package com.dating.ui.registration

import com.dating.interactors.GeoDataSender
import com.dating.ui.base.BaseActivity
import com.dating.ui.treba.InRoute
import com.dating.util.Utils
import com.dating.util.bindRouter
import com.dating.viper.NextObserver
import com.dating.viper.Router
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject


/**
 *   Created by dakishin@gmail.com
 */




sealed class ToRoute {
    class NO_LOCATION : ToRoute()
    class NEAR_ME_LIST : ToRoute()
    class UPDATE : ToRoute()
    class ASK_ADMIN : ToRoute()
}


class RegistrationRouter(val activity: BaseActivity, override val bag: CompositeDisposable, val geoModule: GeoDataSender) : Router<ToRoute, InRoute> {

    override val toRoute: PublishSubject<ToRoute> = PublishSubject.create<ToRoute>()
    override val inRoute: PublishSubject<InRoute> = PublishSubject.create<InRoute>()

    val TAG = RegistrationRouter::class.java.name


    init {



        toRoute
            .subscribeWith(NextObserver {
                when (it) {

                    is ToRoute.NO_LOCATION -> {
//                        activity.presentFragment(NearMeNoCoordFragment.create(), true, true)
                    }

                    is ToRoute.NEAR_ME_LIST -> {
//                        activity.presentFragment(NearMeListFragment.create(), true, true)
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