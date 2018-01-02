package module.christian.ru.dating.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_create_treba_step1.*
import module.christian.ru.dating.R
import module.christian.ru.dating.activity.TrebaActivity
import module.christian.ru.dating.model.TrebaType
import module.christian.ru.dating.widget.TabsWidget


/**
 *   Created by dakishin@gmail.com
 */

val GOOGLE_INAPP_API_VERSION = 3
val PRODUCT_SKU = "test_inapp2"
val PRODUCT_TYPE = "inapp"


class TrebaCreateFragmentStep1 : BaseFragment() {


    var selectedTrebaType: TrebaType? = null


    companion object {
        val TAG = TrebaCreateFragmentStep1::javaClass.name

        @JvmStatic
        fun create(): TrebaCreateFragmentStep1 = TrebaCreateFragmentStep1()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_treba_step1, container, false)
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
                    TabsWidget.ActiveTab.CENTER -> {
                        (activity as TrebaActivity).setFragment(TrebaHistoryFragment.create())
                    }

                }
            }
        }
        tabs.selectTab(TabsWidget.ActiveTab.LEFT)


        val trebs = ArrayList<String>()
        TrebaType.values().forEach {
            trebs.add(getString(it.nameRes))
        }
        trebsList.adapter = ArrayAdapter(activity, R.layout.trebs_list_item, android.R.id.text1, trebs)

        trebsList.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTrebaType = TrebaType.values()[position]
            }

        }
        create_treba_button.setOnClickListener({
            goToStep2()
        })

    }


    fun goToStep2() {
        selectedTrebaType ?: return
        (activity as TrebaActivity).setFragment(TrebaCreateFragmentStep2.create().apply {
            this.selectedTrebaType = this@TrebaCreateFragmentStep1.selectedTrebaType!!
        })
    }


}