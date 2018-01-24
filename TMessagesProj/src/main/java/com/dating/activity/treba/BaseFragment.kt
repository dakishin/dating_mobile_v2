package com.dating.activity.treba

import android.app.Fragment
import android.support.v7.widget.Toolbar
import com.dating.api.DatingApi
import com.dating.modules.AppComponentInstance
import com.dating.modules.GeoModule
import com.dating.modules.ProfilePreferences
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */
open class BaseFragment : Fragment() {

    protected fun initActionbarWithBackButton(titleRes: Int) {
        val mToolbar = activity.findViewById(R.id.toolbar) as Toolbar
        mToolbar.setTitle(titleRes)
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        mToolbar.setNavigationOnClickListener { v ->
            activity?.onBackPressed()
        }
    }


    protected fun cancelTask(task: DatingAsynkTask<Any>?) {
        try {

            task?.cancel(true)
        } catch (e: Exception) {

        }
    }


    protected fun getApi(): DatingApi {
        return AppComponentInstance.getAppComponent(activity).getDatingApi()
    }

    protected fun getPreferences(): ProfilePreferences {
        return AppComponentInstance.getAppComponent(activity).getProfilePreferences()
    }

    protected fun getGeoModule(): GeoModule {
        return AppComponentInstance.getAppComponent(activity).getGeoModule()
    }


}