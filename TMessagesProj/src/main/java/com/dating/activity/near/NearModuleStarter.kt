package com.dating.activity.near

import com.dating.activity.near.view.BuySearchFragment
import com.dating.activity.near.view.NearMeListFragment
import com.dating.activity.near.view.NearMeNoCoordFragment
import com.dating.modules.AppComponentInstance
import com.dating.util.ioScheduler
import com.dating.viper.IgnoreErrorsObserver
import org.telegram.ui.LaunchActivity

/**
 *   Created by dakishin@gmail.com
 */
object NearModuleStarter {

    @JvmStatic
    fun start(activity: LaunchActivity) {
        AppComponentInstance.getAppComponent(activity).getDatingApi().createPurchase("test_sku","test_order")
            .ioScheduler()
            .subscribe(IgnoreErrorsObserver {  })

        val hasSearchPurchase = AppComponentInstance.getAppComponent(activity)
            .getProfilePreferences().hasSearchPurchase()

        if (!hasSearchPurchase) {
            activity.presentFragment(BuySearchFragment.create())
            return
        }

        val hasLocation = AppComponentInstance.getAppComponent(activity)
            .getGeoModule().hasLocation()


        if (!hasLocation) {
            activity.presentFragment(NearMeNoCoordFragment.create())
            return
        }

        activity.presentFragment(NearMeListFragment.create())

    }
}