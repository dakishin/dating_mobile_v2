package com.dating.ui.registration

import com.arellomobile.mvp.InjectViewState
import com.dating.api.DatingApi
import com.dating.interactors.ProfilePreferences
import com.dating.ui.base.ApiErrors
import com.dating.ui.base.ApiErrorsPresenter
import com.dating.ui.registration.view.RegistrationView
import com.dating.ui.treba.InRoute
import com.dating.util.bindPresenter
import com.dating.viper.Container
import com.dating.viper.NextObserver
import com.dating.viper.Presenter
import com.dating.viper.Router
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

/**
 *   Created by dakishin@gmail.com
 */
sealed class Action {

    class UPDATE : Action()
}

@InjectViewState
class RegistrationContainer(
    override var viewModel: RegistrationViewModel = RegistrationViewModel()
) : Container<RegistrationViewModel, RegistrationView>()


class RegistrationPresenter(
    val router: Router<ToRoute, InRoute>,
    override val bag: CompositeDisposable,
    val profilePreferences: ProfilePreferences,
    val activity: RegistrationActivity,
    val api: DatingApi
) : Presenter<RegistrationViewModel, RegistrationView, Action, Unit>, ApiErrorsPresenter {

    override lateinit var container: Container<RegistrationViewModel, RegistrationView>

    val TAG = RegistrationPresenter::javaClass.name

    override val action = PublishSubject.create<Action>()

    override val apiErrors = PublishSubject.create<ApiErrors>()

    init {

        action
            .subscribeWith(NextObserver<Action> {

                when (it) {
                    is Action.UPDATE -> {


                    }
                }
            })
            .bindPresenter(this)


        apiErrors
            .subscribeWith(NextObserver<ApiErrors> {
                when (it) {
                    is ApiErrors.INTERNAL_ERROR -> {
                        renderVm {
                            copy(isLoading = false, hasError = true)
                        }
                    }

                    is ApiErrors.UPDATE_NEEDED -> {


                    }
                }
            })
            .bindPresenter(this)
    }


    override fun start() {


    }


}