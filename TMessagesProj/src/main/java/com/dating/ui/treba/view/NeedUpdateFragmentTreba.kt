package com.dating.ui.treba.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.dating.ui.base.BaseFragment
import com.dating.ui.treba.*
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */
class NeedUpdateFragmentTreba : BaseFragment(), TrebaView {


    private lateinit var presenter: TrebaPresenter

    @ProvidePresenter
    fun providePresenter() = TrebaContainer()

    @InjectPresenter
    lateinit var container: TrebaContainer

    private lateinit var binder: Unbinder


    companion object {
        @JvmStatic
        fun create(): NeedUpdateFragmentTreba = NeedUpdateFragmentTreba()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_treba_need_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder = ButterKnife.bind(this, view)
        initActionbarWithBackButton(R.string.update_fragment_title)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = getAppComponent().trebaComponent(TrebaModule(activity as TrebaActivity))
        presenter = component.presenter()
        presenter.container = container
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binder.unbind()
    }


    override fun renderViewModel(viewModel: TrebaViewModel) {

    }

    @OnClick(R.id.button_update)
    fun onClickUpdate() {
        presenter.action.onNext(Action.UPDATE())

    }


}