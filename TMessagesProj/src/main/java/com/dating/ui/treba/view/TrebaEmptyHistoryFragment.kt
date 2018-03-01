package com.dating.ui.treba.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.dating.ui.base.BaseFragment
import com.dating.ui.treba.*
import com.dating.widget.TabsWidget
import kotlinx.android.synthetic.main.fragment_treba_empty_history.*
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */
class TrebaEmptyHistoryFragment : BaseFragment(), TrebaView {


    private lateinit var presenter: TrebaPresenter

    @ProvidePresenter
    fun providePresenter() = TrebaContainer()

    @InjectPresenter
    lateinit var container: TrebaContainer



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
                        presenter.action.onNext(Action.ShowAboutTAb())
                    }
                    TabsWidget.ActiveTab.LEFT -> {
                        presenter.action.onNext(Action.ShowCreateTrebaTab())
                    }



                }
            }
        }
        tabs.selectTab(TabsWidget.ActiveTab.CENTER)
        go_to_create_treba.setOnClickListener({ onClickCreateTreba() })


    }

    fun onClickCreateTreba() {
        presenter.action.onNext(Action.ShowCreateTrebaTab())
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