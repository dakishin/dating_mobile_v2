package com.dating.ui.near

import com.dating.modules.AppComponentInstance
import com.dating.ui.near.view.BuySearchFragment
import com.dating.ui.near.view.NearMeListFragment
import com.dating.ui.near.view.NearMeNoCoordFragment
import com.dating.util.Utils
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

        if (!hasSearchPurchase && !Utils.isModerator(telegramId)) {
            activity.presentFragment(BuySearchFragment.create())
            return
        }

        val hasLocation = AppComponentInstance.getAppComponent(activity)
            .saveLocationInteractor().hasLocation()


        if (!hasLocation) {
            activity.presentFragment(NearMeNoCoordFragment.create())
            return
        }

        activity.presentFragment(NearMeListFragment.create())

    }
}