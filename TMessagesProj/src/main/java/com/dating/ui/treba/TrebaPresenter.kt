package com.dating.ui.treba

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.dating.api.DatingApi
import com.dating.billing.ConsumeInteractor
import com.dating.billing.GetPurchasesInteractor
import com.dating.interactors.ProfilePreferences
import com.dating.model.Treba
import com.dating.model.TrebaType
import com.dating.modules.BuyInteractor
import com.dating.ui.base.ApiErrors
import com.dating.ui.base.ApiErrorsPresenter
import com.dating.ui.base.ApiObserver
import com.dating.ui.treba.view.TrebaView
import com.dating.util.bindPresenter
import com.dating.util.ioScheduler
import com.dating.viper.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 *   Created by dakishin@gmail.com
 */
sealed class Action {


    class ShowHistoryTab() : Action()
    class ShowAboutTAb() : Action()
    class ShowCreateTrebaTab() : Action()


    class ClickCreateTrebaStep2(val selectedTrebaType: TrebaType?) : Action()
    class ClickCreateTrebaStep3(val selectedTrebaType: TrebaType, val names: List<String>) : Action()

    class ClickCreateTreba(val selectedTrebaType: TrebaType, val names: List<String>, val selectedPriestUuid: String?) : Action()


    class BUY(val sku: String) : Action()

    class AskAdmin : Action()
    class UPDATE : Action()
    class DownloadTretbaList : Action()
    class CONSUME : Action()
}

@InjectViewState
class TrebaContainer(
    override var viewModel: TrebaViewModel = TrebaViewModel()
) : Container<TrebaViewModel, TrebaView>()


class TrebaPresenter constructor(
    val router: Router<ToRoute, InRoute>,
    override val bag: CompositeDisposable,
    val profilePreferences: ProfilePreferences,
    val activity: TrebaActivity,
    val api: DatingApi,
    val purchaseInteractor: GetPurchasesInteractor,
    val buyInteractor: BuyInteractor,
    val consumeInteractor: ConsumeInteractor
) : Presenter<TrebaViewModel, TrebaView, Action, Unit>, ApiErrorsPresenter {

    override lateinit var container: Container<TrebaViewModel, TrebaView>

    val TAG = TrebaPresenter::javaClass.name

    override val action = PublishSubject.create<Action>()

    override val apiErrors = PublishSubject.create<ApiErrors>()

    init {

        action
            .subscribeWith(NextObserver<Action> {

                when (it) {

                    is Action.DownloadTretbaList -> {
                        renderVm {
                            copy(isLoading = true, hasError = false)
                        }
                        api
                            .getUserTrebs()
                            .ioScheduler()
                            .subscribeWith(
                                object : ApiObserver<List<Treba>>(this, profilePreferences) {
                                    override fun onNext(next: List<Treba>) {
                                        super.onNext(next)
                                        if (next.isEmpty()) {
                                            router.toRoute.onNext(ToRoute.SHOW_EMPTY_HISTORY())
                                        } else {
                                            renderVm {
                                                copy(isLoading = false, trebasHistory = next, hasError = false)
                                            }
                                        }

                                    }
                                }
                            ).bindPresenter(this)
                    }

                    is Action.ClickCreateTrebaStep2 -> {
                        it.selectedTrebaType ?: return@NextObserver
                        router.toRoute.onNext(ToRoute.CREATE_TREBA_STEP2(it.selectedTrebaType))
                    }

                    is Action.ClickCreateTrebaStep3 -> {
                        router.toRoute.onNext(ToRoute.CREATE_TREBA_STEP3(it.selectedTrebaType, it.names))
                    }

                    is Action.ClickCreateTreba -> {
                        renderVm {
                            copy(showBuyDialog = true, selectedTrebaType = it.selectedTrebaType, names = it.names,
                                selectedPriestUuid = it.selectedPriestUuid, hasError = false)
                        }
                    }

                    is Action.BUY -> {
                        renderVm {
                            copy(isLoading = false, showBuyDialog = false, hasError = false)
                        }
                        val sku = it.sku
                        purchaseInteractor
                            .downloadPurchases()
                            .flatMap { list ->
                                list.forEach {
                                    if (sku == it.sku) {
                                        return@flatMap consumeInteractor.consumeByPurchaseToken(it.purchaseToken)
                                    }
                                }
                                Observable.just(0)
                            }
                            .doOnNext { Log.d(TAG, "start buying") }
                            .flatMap {
                                buyInteractor.buy(sku)
                            }

                            .observeOn(Schedulers.io())
                            .flatMap { purchaseEvent ->
                                purchaseEvent.purchases.findLast { it.sku == sku }!!
                                api.createTreba(viewModel.selectedTrebaType!!, viewModel.names, viewModel.selectedPriestUuid!!)
                            }
                            .ioScheduler()
                            .subscribeWith(
                                object : ApiObserver<Any>(this, profilePreferences) {
                                    override fun onNext(next: Any) {
                                        super.onNext(next)
                                        router.toRoute.onNext(ToRoute.SHOW_HISTORY_TAB())
                                    }
                                }
                            ).bindPresenter(this)

                    }

                    is Action.CONSUME -> {
                        renderVm {
                            copy(isLoading = true)
                        }
                        purchaseInteractor
                            .downloadPurchases()
                            .flatMap {
                                Observable.fromIterable(it)
                            }
                            .flatMap {
                                consumeInteractor.consumeByPurchaseToken(it.purchaseToken)
                            }
                            .ioScheduler()
                            .subscribeWith(IgnoreErrorsObserver {
                                Log.e(TAG, "end subscriber")
                                renderVm {
                                    copy(isLoading = false)
                                }
                            })
                            .bindPresenter(this)

                    }

                    is Action.ShowHistoryTab -> {
                        router.toRoute.onNext(ToRoute.SHOW_HISTORY_TAB())
                    }

                    is Action.ShowCreateTrebaTab -> {
                        router.toRoute.onNext(ToRoute.SHOW_CREATE_TAB())
                    }


                    is Action.ShowAboutTAb -> {
                        router.toRoute.onNext(ToRoute.SHOW_INFO_TAB())
                    }

                    is Action.AskAdmin -> {
                        router.toRoute.onNext(ToRoute.ASK_ADMIN())
                    }

                    is Action.UPDATE -> {
                        router.toRoute.onNext(ToRoute.GO_TO_GOOGLE_PLAY())
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
                        router.toRoute.onNext(ToRoute.SHOW_NEED_UPDATE())
                    }
                }
            })
            .bindPresenter(this)
    }


    override fun start() {
//        renderVm {
//            copy(isLoading = false, hasError = true)
//        }

    }


}