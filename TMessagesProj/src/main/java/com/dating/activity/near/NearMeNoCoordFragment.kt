package com.dating.activity.near

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.ActionBar
import org.telegram.ui.ActionBar.BaseFragment

/**
 *   Created by dakishin@gmail.com
 */
class NearMeNoCoordFragment : BaseFragment() {

    companion object {

        fun create(): NearMeNoCoordFragment {
            return NearMeNoCoordFragment()
        }
    }


    override fun createView(context: Context): View? {

        actionBar.setBackButtonImage(R.drawable.ic_ab_back)
        actionBar.setAllowOverlayTitle(true)
        actionBar.setTitle(context.getString(R.string.who_near_menu))
        actionBar.setActionBarMenuOnItemClick(object : ActionBar.ActionBarMenuOnItemClick() {
            override fun onItemClick(id: Int) {
                if (id == -1) {
                    finishFragment()
                }
            }
        })
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        fragmentView = inflater.inflate(R.layout.dating_near_me_no_coord, null)


        return fragmentView

    }


}