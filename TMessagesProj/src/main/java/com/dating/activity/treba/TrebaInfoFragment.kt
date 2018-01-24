package com.dating.activity.treba

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dating.widget.TabsWidget
import kotlinx.android.synthetic.main.fragment_info_treba.*
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */
class TrebaInfoFragment : BaseFragment() {

    val TAG = "TrebaInfoFragment"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_info_treba, container, false)
    }



    companion object {
        @JvmStatic
        fun create(): TrebaInfoFragment = TrebaInfoFragment()
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionbarWithBackButton(R.string.treba_menu)
        tabs.initTabs(R.string.trebi_title_new, R.string.trebi_title_info, R.string.trebi_title_history)

        tabs.onTabSelected = object : TabsWidget.OnTabSelected {
            override fun onSelect(tab: TabsWidget.ActiveTab) {
                when (tab) {
                    TabsWidget.ActiveTab.LEFT -> {
                        (activity as TrebaActivity).setFragment(TrebaCreateFragmentStep1.create())
                    }
                    TabsWidget.ActiveTab.CENTER -> {
                        (activity as TrebaActivity).setFragment(TrebaHistoryFragment.create())
                    }

                }
            }
        }
        tabs.selectTab(TabsWidget.ActiveTab.RIGHT)
        evgeniy_view.setOnClickListener({clickYevgeniy()})
        nilolay_view.setOnClickListener({clickNikolay()})
    }



    fun clickYevgeniy() {
//        val yevgeniy = ProfilePreferences.getYevgeniy()
//        yevgeniy ?: return
//        UserViewActivity.start(activity, yevgeniy)

    }

    fun clickNikolay() {
//        val nikolay = ProfilePreferences.getNikolay()
//        nikolay ?: return
//        UserViewActivity.start(activity, nikolay)
    }


}