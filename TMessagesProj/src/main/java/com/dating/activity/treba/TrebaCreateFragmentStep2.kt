package com.dating.activity.treba

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.dating.model.TrebaType
import com.dating.util.Utils
import com.dating.widget.TabsWidget
import kotlinx.android.synthetic.main.fragment_create_treba_step2.*
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */


class TrebaCreateFragmentStep2 : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_treba_step2, container, false)
    }




    private val nameViews: List<EditText> by lazy {
        mutableListOf(
            nameEditText1, nameEditText2, nameEditText3, nameEditText4, nameEditText5, nameEditText6
        )
    }

    lateinit var selectedTrebaType: TrebaType


    companion object {
        @JvmStatic
        fun create(): TrebaCreateFragmentStep2 = TrebaCreateFragmentStep2()
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
        create_treba_button.setOnClickListener({ createTreba() })
        cancel_treba_button.setOnClickListener({cancelCreateTreba()})

    }


    fun createTreba() {
        val nameValues = getNameValues()
        if (nameValues.isEmpty()) {
            nameHolder1.error = getString(R.string.create_treba_error_name)
            return
        }

        (activity as TrebaActivity).setFragment(TrebaCreateFragmentStep3.create().apply {
            this.selectedTrebaType = this@TrebaCreateFragmentStep2.selectedTrebaType!!
            this.selectedNames = nameValues
        })


    }


    private fun getNameValues(): List<String> {
        val names = mutableListOf<String>()
        nameViews.forEach {
            if (Utils.isNotBlank(it.text.toString())) {
                names.add(it.text.toString())
            }
        }
        return names
    }

    private fun clearForm() {
        nameViews.forEach {
            it.text = null
            Utils.hideKeyBoard(activity, it)
        }
    }


    override fun onStop() {
        nameViews.forEach { Utils.hideKeyBoard(activity, it) }
        super.onStop()
    }


    fun cancelCreateTreba() {
        (activity as TrebaActivity).setFragment(TrebaCreateFragmentStep1.create())
    }
}