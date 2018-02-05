package com.dating.activity.near

import android.Manifest
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.dating.util.Utils
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.ActionBar
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.LaunchActivity


/**
 *   Created by dakishin@gmail.com
 */

class NearMeNoCoordFragment : BaseFragment(), NearMeView {

    @ProvidePresenter
    fun providePresenter() = NearMeContainer()


    @InjectPresenter
    lateinit var container: NearMeContainer


    private var mMvpDelegate: MvpDelegate<NearMeNoCoordFragment>? = null


    companion object {

        const val REQUEST_GEO_PERMISSION: Int = -221

        fun create(): NearMeNoCoordFragment {
            return NearMeNoCoordFragment().apply {
                getMvpDelegate().onCreate()
            }
        }
    }


    fun getMvpDelegate(): MvpDelegate<NearMeNoCoordFragment> {
        if (mMvpDelegate == null) {
            mMvpDelegate = MvpDelegate<NearMeNoCoordFragment>(this)
        }
        return mMvpDelegate!!
    }

    private lateinit var presenter: NearMePresenter

    override fun createView(context: Context): View? {

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


        getMvpDelegate().onAttach()

        val component = appComponent.nearMeComponent(NearMeModule(parentActivity as LaunchActivity, getMvpDelegate()))


        presenter = component.presenter()
        presenter.container = container

        presenter.start()


//        bag.add(
//            appComponent.getGeoModule()
//                .geoPermissionGranted
//                .flatMap {
//                    appComponent.getGeoModule().sendGeoDataObserver
//                }
//                .subscribe({
//                    if (appComponent.getGeoModule().hasLocation()) {
//
//                    }
//                }, {}, {})
//        )

        return fragmentView

    }


    private fun requestLocation() {
        if (!Utils.isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION, parentActivity)) {
            Utils.requestPermission(parentActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_GEO_PERMISSION)
            return
        }

    }


    override fun onFragmentDestroy() {
        presenter.onDestroy()
        getMvpDelegate().onDestroyView()
        getMvpDelegate().onDestroy()
        super.onFragmentDestroy()
    }


    override fun renderViewModel(viewModel: NearMeViewModel) {

        val i = 0;

    }

}