package com.dating.ui.near

import com.arellomobile.mvp.InjectViewState
import com.dating.api.DatingApi
import com.dating.model.CompoundUser
import com.dating.modules.GeoModule
import com.dating.modules.ProfilePreferences
import com.dating.ui.base.ApiErrors
import com.dating.ui.base.ApiErrorsPresenter
import com.dating.ui.base.ApiObserver
import com.dating.ui.base.PurchaseInteractor
import com.dating.ui.near.view.NearMeNoCoordFragment
import com.dating.ui.near.view.NearMeView
import com.dating.ui.near.view.NeedUpdateFragment
import com.dating.ui.near.view.searchSkus
import com.dating.ui.treba.InRoute
import com.dating.util.Utils
import com.dating.util.bindPresenter
import com.dating.util.ioScheduler
import com.dating.viper.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.telegram.ui.LaunchActivity
import java.util.concurrent.TimeUnit

/**
 *   Created by dakishin@gmail.com
 */
sealed class Action {
    class BUY(val sku: String) : Action()
    class DOWNLOAD_PURCHASES : Action()
    class SHOW_USER_LIST(val chatId: Int) : Action()
    class CLICK_GET_LOCATION : Action()

    class UPDATE : Action()
    class AskAdmin : Action()
    class UNEXPECTED_ERROR : Action()

}

@InjectViewState
class NearMeContainer(
    override var viewModel: NearMeViewModel = NearMeViewModel()
) : Container<NearMeViewModel, NearMeView>()


class NearMePresenter(
    val router: Router<ToRoute, InRoute>,
    override val bag: CompositeDisposable,
    val geoModule: GeoModule,
    val purchaceInteractor: PurchaseInteractor,
    val profilePreferences: ProfilePreferences,
    val activity: LaunchActivity,
    val api: DatingApi,
    val nearMeListInteractor: NearMeListInteractor
) : Presenter<NearMeViewModel, NearMeView, Action, Unit>, ApiErrorsPresenter {

    override lateinit var container: Container<NearMeViewModel, NearMeView>

    val TAG = NearMePresenter::javaClass.name

    override val apiErrors = PublishSubject.create<ApiErrors>()

    override val action = PublishSubject.create<Action>()

    init {

        action
            .subscribeWith(NextObserver<Action> {

                when (it) {
                    is Action.SHOW_USER_LIST -> {
                        nearMeListInteractor
                            .loadUsers(it.chatId)
                            .ioScheduler()
                            .subscribeWith(object : ApiObserver<List<CompoundUser>>(this@NearMePresenter) {
                                override fun onNext(next: List<CompoundUser>) {
                                    super.onNext(next)
                                    renderVm {
                                        copy(isLoading = false, users = next, hasError = false)
                                    }
                                }

                            })
                            .bindPresenter(this)


                    }

                    is Action.CLICK_GET_LOCATION -> {
                        renderVm {
                            copy(isLoading = true)
                        }

                        geoModule
                            .geoPermissionGranted
                            .flatMap {
                                geoModule.sendGeoDataObserver
                            }
                            .timeout(10, TimeUnit.SECONDS)
                            .ioScheduler()
                            .subscribeWith(IgnoreErrorsObserver {
                                renderVm {
                                    copy(isLoading = false)
                                }
                                if (geoModule.hasLocation()) {
                                    router.toRoute.onNext(ToRoute.NEAR_ME_LIST())
                                } else {
                                    router.toRoute.onNext(ToRoute.NO_LOCATION())
                                }
                            })
                            .bindPresenter(this)

                        if (!Utils.isGeoPermissionGranted(activity)) {
                            Utils.requestGeoPermission(activity, NearMeNoCoordFragment.REQUEST_GEO_PERMISSION)
                        } else {
                            geoModule.notifyPermissionGranted()
                        }

                    }
                    is Action.BUY -> {
                        renderVm {
                            copy(isLoading = true)
                        }

                        val sku = it.sku
                        purchaceInteractor
                            .buy(sku)
                            .observeOn(Schedulers.io())
                            .flatMap { purchaseEvent ->
                                val purchase = purchaseEvent.purchases?.findLast { it.sku == sku }
                                purchase?.let {
                                    profilePreferences.saveHasSearchPurchase(true)
                                    api.createPurchase(purchase.sku, purchase.orderId)
                                }

                            }
                            .ioScheduler()
                            .subscribeWith(
                                object : ApiObserver<Any>(this) {
                                    override fun onNext(next: Any) {
                                        super.onNext(next)
                                        if (profilePreferences.hasSearchPurchase()) {
                                            router.toRoute.onNext(ToRoute.NEAR_ME_LIST())
                                        }
                                    }

                                    override fun onComplete() {
                                        onError(RuntimeException("complete error"))
                                    }

                                }
                            ).bindPresenter(this)
                    }

                    is Action.DOWNLOAD_PURCHASES -> {
                        renderVm {
                            copy(isLoading = true)
                        }
                        purchaceInteractor
                            .downloadPurchases()
                            .map { items ->
                                val hasSearchPurchase = items.find { searchSkus.contains(it.sku) } != null
                                if (hasSearchPurchase) {
                                    profilePreferences.saveHasSearchPurchase(true)
                                }
                            }
                            .timeout(20, TimeUnit.SECONDS)
                            .ioScheduler()
                            .subscribeWith(IgnoreErrorsObserver {
                                renderVm {
                                    copy(isLoading = false)
                                }
                                if (profilePreferences.hasSearchPurchase()) {
                                    router.toRoute.onNext(ToRoute.NEAR_ME_LIST())
                                }
                            })
                            .bindPresenter(this)
                    }


                    is Action.AskAdmin -> {
                        router.toRoute.onNext(ToRoute.ASK_ADMIN())
                    }

                    is Action.UPDATE -> {
                        router.toRoute.onNext(ToRoute.UPDATE())
                    }
                }
            })
            .bindPresenter(this)


        apiErrors
            .subscribeWith(NextObserver {
                when (it) {
                    is ApiErrors.INTERNAL_ERROR -> {
                        renderVm {
                            copy(isLoading = false, hasError = true)
                        }
                    }
                    is ApiErrors.UPDATE_NEEDED -> {
                        renderVm {
                            copy(isLoading = true)
                        }
                        Observable.interval(1500, TimeUnit.MILLISECONDS)
                            .ioScheduler()
                            .subscribeWith(NextObserver {
                                activity.presentFragment(NeedUpdateFragment.create(), true, false)
                            })
                            .bindPresenter(this)

                    }
                }
            })
            .bindPresenter(this)


    }


    override fun start() {


    }


}