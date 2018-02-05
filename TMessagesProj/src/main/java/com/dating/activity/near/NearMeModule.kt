package com.dating.activity.near

import android.app.Activity
import com.arellomobile.mvp.MvpDelegate
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import org.telegram.ui.LaunchActivity

/**
 *   Created by dakishin@gmail.com
 */
@Module
class NearMeModule(val activity: LaunchActivity,val delegate: MvpDelegate<NearMeNoCoordFragment>) {

    val bag = CompositeDisposable()

    @Provides
    @NearMeScope
    fun provideRouter(): NearMeRouter
        = NearMeRouter(activity, bag)


    @Provides
    @NearMeScope
    fun provideInteractor()
        = NearMeInteractor()


    @Provides
    @NearMeScope
    fun providePresenter(interactor: NearMeInteractor, router: NearMeRouter)
        = NearMePresenter(interactor, router, bag)

    @Provides
    @NearMeScope
    fun provideActivity(): Activity = activity


    @Provides
    @NearMeScope
    fun provideDelegate(): MvpDelegate<NearMeNoCoordFragment> = delegate


}