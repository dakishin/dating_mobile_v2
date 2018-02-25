package com.dating.ui.treba

import com.dating.model.TrebaType
import com.dating.modules.GeoModule
import com.dating.ui.treba.view.*
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
    class CREATE_TREBA_STEP2(val selectedTrebaType: TrebaType) : ToRoute()
    class CREATE_TREBA_STEP3(val selectedTrebaType: TrebaType, val names: List<String>) : ToRoute()

    class SHOW_CREATE_TAB() : ToRoute()
    class SHOW_HISTORY_TAB() : ToRoute()
    class SHOW_INFO_TAB() : ToRoute()

    class GO_TO_GOOGLE_PLAY : ToRoute()
    class ASK_ADMIN : ToRoute()
    class SHOW_EMPTY_HISTORY : ToRoute()
    class SHOW_NEED_UPDATE : ToRoute()

}

sealed class InRoute {
    class PERMISSION_GRANTED : InRoute()
}


class TrebaRouter(val activity: TrebaActivity, override val bag: CompositeDisposable, val geoModule: GeoModule) : Router<ToRoute, InRoute> {

    override val toRoute: PublishSubject<ToRoute> = PublishSubject.create<ToRoute>()
    override val inRoute: PublishSubject<InRoute> = PublishSubject.create<InRoute>()

    val TAG = TrebaRouter::class.java.name

    init {

        toRoute
            .subscribeWith(NextObserver {
                when (it) {
                    is ToRoute.CREATE_TREBA_STEP2 -> {
                        val selectedTrebaType = it.selectedTrebaType
                        activity.setFragment(TrebaCreateFragmentStep2.create().apply {
                            this.selectedTrebaType = selectedTrebaType
                        })
                    }

                    is ToRoute.CREATE_TREBA_STEP3 -> {
                        val selectedTrebaType = it.selectedTrebaType
                        val names = it.names
                        activity.setFragment(TrebaCreateFragmentStep3.create().apply {
                            this.selectedTrebaType = selectedTrebaType
                            this.selectedNames = names
                        })
                    }


                    is ToRoute.SHOW_CREATE_TAB -> {
                        activity.setFragment(TrebaCreateFragmentStep1.create())

                    }
                    is ToRoute.SHOW_HISTORY_TAB -> {
                        activity.setFragment(TrebaHistoryFragment.create())

                    }

                    is ToRoute.SHOW_INFO_TAB -> {
                        activity.setFragment(TrebaInfoFragment.create())
                    }

                    is ToRoute.SHOW_EMPTY_HISTORY -> {
                        activity.setFragment(TrebaEmptyHistoryFragment.create())
                    }

                    is ToRoute.GO_TO_GOOGLE_PLAY -> {
                        Utils.startUpdate(activity)
                    }
                    is ToRoute.SHOW_NEED_UPDATE -> {
                        activity.setFragment(NeedUpdateFragmentTreba.create())
                    }
                    is ToRoute.ASK_ADMIN -> {
                        Utils.startAskAdmin(activity)
                    }
                }
            })
            .bindRouter(this)


    }


}