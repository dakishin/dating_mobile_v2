package com.dating.ui.near.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.dating.ui.near.*
import com.dating.util.updateVisibility
import com.dating.viper.DatingBaseFragment
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.ActionBar
import org.telegram.ui.LaunchActivity


/**
 *   Created by dakishin@gmail.com
 */
val searchSkus = arrayOf("inapp_search_v2_200", "inapp_search_v2_400", "inapp_search_v2_1000", "test_inapp2", "test_app")

class BuySearchFragment : DatingBaseFragment(), NearMeView {


    @ProvidePresenter
    fun providePresenter() = NearMeContainer()

    @InjectPresenter
    lateinit var container: NearMeContainer

    private lateinit var binder: Unbinder

    @BindView(R.id.progressBar)
    lateinit var progressBar: View

    companion object {


        @JvmStatic
        fun create(): BuySearchFragment {
            return BuySearchFragment()
        }
    }

    private lateinit var presenter: NearMePresenter


    override fun createView(context: Context): View? {
        getMvpDelegate().onCreate()
        getMvpDelegate().onAttach()
        val component = appComponent.nearMeComponent(NearMeModule(parentActivity as LaunchActivity, container))
        presenter = component.presenter()

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
        fragmentView = inflater.inflate(R.layout.dating_pay, null)
        binder = ButterKnife.bind(this, fragmentView)


        presenter.action.onNext(Action.DOWNLOAD_PURCHASES())

        return fragmentView

    }


    @OnClick(R.id.donate_200)
    fun onClickDonate200() {
        presenter.action.onNext(Action.BUY(searchSkus[0]))
    }


    @OnClick(R.id.donate_400)
    fun onClickDonate400() {
        presenter.action.onNext(Action.BUY(searchSkus[1]))
    }


    @OnClick(R.id.donate_1000)
    fun onClickDonate1000() {
        presenter.action.onNext(Action.BUY(searchSkus[2]))
    }

    override fun onFragmentDestroy() {
        presenter.onDestroy()
        binder.unbind()
        super.onFragmentDestroy()
    }


    override fun renderViewModel(viewModel: NearMeViewModel) {
        progressBar.updateVisibility(viewModel.isLoading)

    }


}