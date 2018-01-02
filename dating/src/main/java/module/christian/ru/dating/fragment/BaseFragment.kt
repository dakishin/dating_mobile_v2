package module.christian.ru.dating.fragment

import android.app.Fragment
import android.support.v7.widget.Toolbar
import module.christian.ru.dating.R
import module.christian.ru.dating.api.Api
import module.christian.ru.dating.util.DatingAsynkTask
import modules.AppComponentInstance
import modules.GeoModule
import modules.ProfilePreferences

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


    protected fun cancelTask(createTrebaTask: DatingAsynkTask<Any>?) {

    }


    protected fun getApi(): Api {
        return AppComponentInstance.getAppComponent(activity).getApi()
    }

    protected fun getPreferences(): ProfilePreferences {
        return AppComponentInstance.getAppComponent(activity).getProfilePreferences()
    }

    protected fun getGeoModule(): GeoModule {
        return AppComponentInstance.getAppComponent(activity).getGeoModule()
    }


}