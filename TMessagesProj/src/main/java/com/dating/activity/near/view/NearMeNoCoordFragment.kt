package com.dating.activity.near.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.dating.activity.near.*
import com.dating.util.updateVisibility
import com.dating.viper.DatingBaseFragment
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.ActionBar
import org.telegram.ui.LaunchActivity


/**
 *   Created by dakishin@gmail.com
 */

class NearMeNoCoordFragment : DatingBaseFragment(), NearMeView {

    @ProvidePresenter
    fun providePresenter() = NearMeContainer()

    @InjectPresenter
    lateinit var container: NearMeContainer


    private lateinit var presenter: NearMePresenter


    companion object {

        const val REQUEST_GEO_PERMISSION: Int = 221

        @JvmStatic
        fun create(): NearMeNoCoordFragment {
            return NearMeNoCoordFragment()
        }
    }


    private lateinit var binder: Unbinder

    @BindView(R.id.progressBar)
    lateinit var progressBar: View

    override fun createView(context: Context): View? {

        getMvpDelegate().onCreate()
        getMvpDelegate().onAttach()
        val component = appComponent.nearMeComponent(NearMeModule(parentActivity as LaunchActivity))
        presenter = component.presenter()
        presenter.container = container



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
        fragmentView = inflater.inflate(R.layout.dating_near_me_no_coord, null)
        binder = ButterKnife.bind(this, fragmentView)



        return fragmentView

    }


    @OnClick(R.id.requestLocationButton)
    fun onClickGetLocation() {
        presenter.action.onNext(Action.CLICK_GET_LOCATION())
    }

    override fun onFragmentDestroy() {
        presenter.onDestroy()
        getMvpDelegate().onDestroyView()
        binder.unbind()
        super.onFragmentDestroy()
    }


    override fun renderViewModel(viewModel: NearMeViewModel) {
        progressBar.updateVisibility(viewModel.isLoading)
    }


}