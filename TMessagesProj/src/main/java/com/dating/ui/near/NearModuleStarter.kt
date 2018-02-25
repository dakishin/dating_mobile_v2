package com.dating.ui.near

import com.dating.ui.near.view.BuySearchFragment
import com.dating.ui.near.view.NearMeListFragment
import com.dating.ui.near.view.NearMeNoCoordFragment
import com.dating.ui.near.view.moderatorsIds
import com.dating.modules.AppComponentInstance
import org.telegram.ui.LaunchActivity

/**
 *   Created by dakishin@gmail.com
 */
object NearModuleStarter {

    @JvmStatic
    fun start(activity: LaunchActivity) {

        val hasSearchPurchase = AppComponentInstance.getAppComponent(activity)
            .getProfilePreferences().hasSearchPurchase()

        val telegramId = AppComponentInstance.getAppComponent(activity).getProfilePreferences().getTelegramId() ?: 0

        if (!hasSearchPurchase && !moderatorsIds.contains(telegramId)) {
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