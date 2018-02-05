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

class NeedUpdateFragment : DatingBaseFragment(), NearMeView {


    @ProvidePresenter
    fun providePresenter() = NearMeContainer()

    @InjectPresenter
    lateinit var container: NearMeContainer

    private lateinit var binder: Unbinder

    @BindView(R.id.progressBar)
    lateinit var progressBar: View

    companion object {


        @JvmStatic
        fun create(): NeedUpdateFragment {
            return NeedUpdateFragment()
        }
    }

    private lateinit var presenter: NearMePresenter


    override fun createView(context: Context): View? {
        getMvpDelegate().onCreate()
        getMvpDelegate().onAttach()
        val component = appComponent.nearMeComponent(NearMeModule(parentActivity as LaunchActivity, getMvpDelegate()))
        presenter = component.presenter()
        presenter.container = container



        actionBar.setBackButtonImage(R.drawable.ic_ab_back)
        actionBar.setAllowOverlayTitle(true)
        actionBar.setTitle(context.getString(R.string.update_fragment_title))
        actionBar.setActionBarMenuOnItemClick(object : ActionBar.ActionBarMenuOnItemClick() {
            override fun onItemClick(id: Int) {
                if (id == -1) {
                    finishFragment()
                }
            }
        })

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        fragmentView = inflater.inflate(R.layout.dating_need_update, null)
        binder = ButterKnife.bind(this, fragmentView)


        presenter.action.onNext(Action.DOWNLOAD_PURCHASES())

        return fragmentView

    }


    @OnClick(R.id.button_update)
    fun update() {
        presenter.action.onNext(Action.UPDATE())
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