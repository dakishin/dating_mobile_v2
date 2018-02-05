package com.dating.activity.near

import com.arellomobile.mvp.InjectViewState
import com.dating.viper.Container
import com.dating.viper.Presenter
import com.dating.viper.Router
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

/**
 *   Created by dakishin@gmail.com
 */
sealed class Action {
    class FEED : Action()
    class FAMILY : Action()


}

@InjectViewState
class NearMeContainer(
    override var viewModel: NearMeViewModel = NearMeViewModel()
) : Container<NearMeViewModel, NearMeView>()


class NearMePresenter(
    val interactor: NearMeInteractor,
    val router: Router<Route, Output>,
    override val bag: CompositeDisposable
) : Presenter<NearMeViewModel, NearMeView, Action, Unit> {

    override lateinit var container: Container<NearMeViewModel, NearMeView>


    override fun start() {
        renderVm { copy(isLoading = true) }

    }


    override val action = PublishSubject.create<Action>()
}