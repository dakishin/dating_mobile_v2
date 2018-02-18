package com.dating.ui.treba.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import butterknife.ButterKnife
import butterknife.Unbinder
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.dating.model.TrebaType
import com.dating.ui.treba.*
import com.dating.widget.TabsWidget
import kotlinx.android.synthetic.main.fragment_create_treba_step1.*
import org.telegram.messenger.R


/**
 *   Created by dakishin@gmail.com
 */

class TrebaCreateFragmentStep1 : BaseFragment(), TrebaView {

    private lateinit var presenter: TrebaPresenter

    @ProvidePresenter
    fun providePresenter() = TrebaContainer()

    @InjectPresenter
    lateinit var container: TrebaContainer

    var selectedTrebaType: TrebaType? = null


    private lateinit var binder: Unbinder



    companion object {
        val TAG = TrebaCreateFragmentStep1::javaClass.name

        @JvmStatic
        fun create(): TrebaCreateFragmentStep1 = TrebaCreateFragmentStep1()
    }

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_treba_step1, viewGroup, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder = ButterKnife.bind(this, view)
        initActionbarWithBackButton(R.string.treba_menu)
        tabs.initTabs(R.string.trebi_title_new, R.string.trebi_title_info, R.string.trebi_title_history)
        tabs.onTabSelected = object : TabsWidget.OnTabSelected {
            override fun onSelect(tab: TabsWidget.ActiveTab) {
                when (tab) {
                    TabsWidget.ActiveTab.RIGHT -> {
                        presenter.action.onNext(Action.ShowAboutTAb())
                    }
                    TabsWidget.ActiveTab.CENTER -> {
                        presenter.action.onNext(Action.ShowHistoryTab())
                    }

                }
            }
        }
        tabs.selectTab(TabsWidget.ActiveTab.LEFT)


        val trebs = ArrayList<String>()
        TrebaType.values().forEach {
            trebs.add(getString(it.nameRes))
        }
        trebsList.adapter = ArrayAdapter(activity, R.layout.dating_trebs_list_item, android.R.id.text1, trebs)

        trebsList.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTrebaType = TrebaType.values()[position]
            }

        }
        create_treba_button.setOnClickListener({
            presenter.action.onNext(Action.ClickCreateTrebaStep2(selectedTrebaType))
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binder.unbind()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = getAppComponent().trebaComponent(TrebaModule(activity as TrebaActivity))
        presenter = component.presenter()
        presenter.container = container
    }

    override fun renderViewModel(viewModel: TrebaViewModel) {

    }

}