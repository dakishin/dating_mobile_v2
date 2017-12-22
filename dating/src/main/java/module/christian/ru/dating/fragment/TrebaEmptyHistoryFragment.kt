package module.christian.ru.dating.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_treba_empty_history.*
import module.christian.ru.dating.R
import module.christian.ru.dating.activity.TrebaActivity
import module.christian.ru.dating.widget.TabsWidget

/**
 *   Created by dakishin@gmail.com
 */
class TrebaEmptyHistoryFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_treba_empty_history, container, false)
    }


    companion object {
        @JvmStatic
        fun create(): TrebaEmptyHistoryFragment = TrebaEmptyHistoryFragment()
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionbarWithBackButton(R.string.treba_menu)
        tabs.initTabs(R.string.trebi_title_new, R.string.trebi_title_info, R.string.trebi_title_history)

        tabs.onTabSelected = object : TabsWidget.OnTabSelected {
            override fun onSelect(tab: TabsWidget.ActiveTab) {

                when (tab) {
                    TabsWidget.ActiveTab.RIGHT -> {
                        (activity as TrebaActivity).setFragment(TrebaInfoFragment.create())
                    }
                    TabsWidget.ActiveTab.LEFT -> {
                        (activity as TrebaActivity).setFragment(TrebaCreateFragmentStep1.create())
                    }

                }
            }
        }
        tabs.selectTab(TabsWidget.ActiveTab.CENTER)
        go_to_create_treba.setOnClickListener({ onClickCreateTreba() })


    }

    fun onClickCreateTreba() {
        (activity as TrebaActivity).setFragment(TrebaCreateFragmentStep1.create())
    }


}