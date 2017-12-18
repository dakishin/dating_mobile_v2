package module.christian.ru.dating.fragment

import android.app.Fragment
import android.support.v7.widget.Toolbar
import module.christian.ru.dating.R

/**
 *   Created by dakishin@gmail.com
 */
open class BaseFragment : Fragment() {

    protected fun initActionbarWithBackButton(titleRes: Int) {
        val mToolbar = activity.findViewById(R.id.toolbar) as Toolbar
//        activity.setActionBar(mToolbar)
//        val supportActionBar = getMainActivity().getSupportActionBar()
        mToolbar.setTitle(titleRes)
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        mToolbar.setNavigationOnClickListener { v -> activity.onBackPressed() }
    }

//    protected fun initActionbar(titleRes: Int) {
//        val mToolbar = activity.findViewById(getToolbarId())
//        getMainActivity().setSupportActionBar(mToolbar)
//        val supportActionBar = getMainActivity().getSupportActionBar()
//        supportActionBar!!.setTitle(titleRes)
//        mToolbar.setNavigationIcon(R.drawable.ic_drawer_romash)
//    }
}