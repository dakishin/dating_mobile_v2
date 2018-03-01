package com.dating.ui.treba.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.dating.model.PRIEST_EVGEN_UUID
import com.dating.model.PRIEST_NOKOLAY_UUID
import com.dating.model.TrebaType
import com.dating.ui.base.BaseFragment
import com.dating.ui.treba.*
import com.dating.util.updateVisibility
import com.dating.widget.SelectTrebaPriceDialog
import com.dating.widget.TabsWidget
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_create_treba_step3.*
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */


class TrebaCreateFragmentStep3 : BaseFragment(), TrebaView {

    private lateinit var presenter: TrebaPresenter

    @ProvidePresenter
    fun providePresenter() = TrebaContainer()

    @InjectPresenter
    lateinit var container: TrebaContainer

    lateinit var selectedTrebaType: TrebaType
    lateinit var selectedNames: List<String>

    var selectedPriestUuid: String? = null

    @BindView(R.id.progressBar)
    lateinit var progressBar: View

    private lateinit var binder: Unbinder

    companion object {
        val TAG = TrebaCreateFragmentStep3::javaClass.name

        @JvmStatic
        fun create(): TrebaCreateFragmentStep3 = TrebaCreateFragmentStep3()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_treba_step3, container, false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binder.unbind()
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

        cancel_treba_button.setOnClickListener({ cancelCreateTreba() })
        yevgen_layout.setOnClickListener({ chooseYevgen() })
        nikolay_layout.setOnClickListener({ chooseNikolay() })
        create_treba_button.setOnClickListener({ createTreba() })

    }


    fun createTreba() {
        if (selectedPriestUuid == null) {
            Toast.makeText(activity, getString(R.string.choose_church), Toast.LENGTH_SHORT).show()
            return
        }
        presenter.action.onNext(Action.ClickCreateTreba(selectedTrebaType, selectedNames, selectedPriestUuid))

    }



    fun cancelCreateTreba() {
        presenter.action.onNext(Action.ShowCreateTrebaTab())
    }

    fun chooseYevgen() {
        selectedPriestUuid = PRIEST_EVGEN_UUID
        (yevgenTextView as CheckedTextView).isChecked = true
        (nikolayTextView as CheckedTextView).isChecked = false

    }


    fun chooseNikolay() {
        selectedPriestUuid = PRIEST_NOKOLAY_UUID
        (nikolayTextView as CheckedTextView).isChecked = true
        (yevgenTextView as CheckedTextView).isChecked = false
    }


    var selectTrebaPriceDialog: SelectTrebaPriceDialog? = null

    override fun renderViewModel(viewModel: TrebaViewModel) {
        progressBar.updateVisibility(viewModel.isLoading)

        if (viewModel.showBuyDialog) {
            selectTrebaPriceDialog = SelectTrebaPriceDialog()
            selectTrebaPriceDialog
                ?.apply {
                    prices = this@TrebaCreateFragmentStep3.activity.resources.getStringArray(selectedTrebaType.prices).toList()
                    clickItemListener = Consumer<Int> { position ->
                        val sku = activity.resources.getStringArray(selectedTrebaType.skus)[position]
                        presenter.action.onNext(Action.BUY(sku))
//                        presenter.action.onNext(Action.BUY("test_inapp2"))
                    }
                }
                ?.show(fragmentManager, "SelectTrebaTypeDialog")
        } else {
            selectTrebaPriceDialog?.dismiss()
            selectTrebaPriceDialog = null
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = getAppComponent().trebaComponent(TrebaModule(activity as TrebaActivity))
        presenter = component.presenter()
        presenter.container = container
    }
}