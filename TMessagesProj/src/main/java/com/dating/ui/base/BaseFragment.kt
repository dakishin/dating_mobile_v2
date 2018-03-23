package com.dating.ui.base

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.MvpDelegate
import com.dating.api.DatingApi
import com.dating.interactors.ProfilePreferences
import com.dating.interactors.SaveLocationInteractor
import com.dating.modules.AppComponent
import com.dating.modules.AppComponentInstance
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

    protected fun getApi(): DatingApi {
        return AppComponentInstance.getAppComponent(activity).getDatingApi()
    }

    protected fun getPreferences(): ProfilePreferences {
        return AppComponentInstance.getAppComponent(activity).getProfilePreferences()
    }

    protected fun getAppComponent(): AppComponent {
        return AppComponentInstance.getAppComponent(activity)
    }

    protected fun getGeoModule(): SaveLocationInteractor {
        return AppComponentInstance.getAppComponent(activity).saveLocationInteractor()
    }


    private var mIsStateSaved: Boolean = false

    private var mMvpDelegate: MvpDelegate<out BaseFragment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getMvpDelegate().onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        mIsStateSaved = false

        getMvpDelegate().onAttach()
    }

    override fun onResume() {
        super.onResume()

        mIsStateSaved = false

        getMvpDelegate().onAttach()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        mIsStateSaved = true

        getMvpDelegate().onSaveInstanceState(outState)
        getMvpDelegate().onDetach()
    }

    override fun onStop() {
        super.onStop()

        getMvpDelegate().onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        getMvpDelegate().onDetach()
        getMvpDelegate().onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()

        //We leave the screen and respectively all fragments will be destroyed
        if (activity.isFinishing) {
            getMvpDelegate().onDestroy()
            return
        }

        // When we rotate device isRemoving() return true for fragment placed in backstack
        // http://stackoverflow.com/questions/34649126/fragment-back-stack-and-isremoving
        if (mIsStateSaved) {
            mIsStateSaved = false
            return
        }

        // See https://github.com/Arello-Mobile/Moxy/issues/24
        var anyParentIsRemoving = false
        var parent = parentFragment
        while (!anyParentIsRemoving && parent != null) {
            anyParentIsRemoving = parent.isRemoving
            parent = parent.parentFragment
        }

        if (isRemoving || anyParentIsRemoving) {
            getMvpDelegate().onDestroy()
        }
    }

    /**
     * @return The [MvpDelegate] being used by this Fragment.
     */
    fun getMvpDelegate(): MvpDelegate<*> {
        if (mMvpDelegate == null) {
            mMvpDelegate = MvpDelegate<BaseFragment>(this)
        }

        return mMvpDelegate!!
    }

}