package com.dating.viper

import com.arellomobile.mvp.MvpDelegate
import org.telegram.ui.ActionBar.BaseFragment

/**
 *   Created by dakishin@gmail.com
 */
open class DatingBaseFragment : BaseFragment(){
    private var mMvpDelegate: MvpDelegate<Any>? = null

    fun getMvpDelegate(): MvpDelegate<Any> {
        if (mMvpDelegate == null) {
            mMvpDelegate = MvpDelegate<Any>(this)
        }
        return mMvpDelegate!!
    }





    override fun onFragmentDestroy() {
        getMvpDelegate().onDestroyView()
        getMvpDelegate().onDestroy()
        super.onFragmentDestroy()
    }
}