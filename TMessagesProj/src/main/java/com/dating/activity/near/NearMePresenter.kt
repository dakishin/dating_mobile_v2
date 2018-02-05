package com.dating.activity.near

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.dating.activity.near.view.NearMeNoCoordFragment
import com.dating.activity.near.view.NearMeView
import com.dating.activity.near.view.NeedUpdateFragment
import com.dating.activity.near.view.searchSkus
import com.dating.api.DatingApi
import com.dating.model.CompoundUser
import com.dating.modules.GeoModule
import com.dating.modules.ProfilePreferences
import com.dating.util.Utils
import com.dating.util.bindPresenter
import com.dating.util.ioScheduler
import com.dating.viper.*
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
    class CONSUME(val sku: String) : Action()
    class UPDATE : Action()
}

@InjectViewState
class NearMeContainer(
    override var viewModel: NearMeViewModel = NearMeViewModel()
) : Container<NearMeViewModel, NearMeView>()


class NearMePresenter(
    val router: Router<ToRoute, InRoute, ChangeViewRoute>,
    override val bag: CompositeDisposable,
    val geoModule: GeoModule,
    val purchaceInteractor: PurchaseInteractor,
    val profilePreferences: ProfilePreferences,
    val activity: LaunchActivity,
    val api: DatingApi,
    val nearMeListInteractor: NearMeListInteractor
) : Presenter<NearMeViewModel, NearMeView, Action, Unit> {

    override lateinit var container: Container<NearMeViewModel, NearMeView>

    val TAG = NearMePresenter::javaClass.name

    override val action = PublishSubject.create<Action>()

    init {

        action
            .subscribeWith(NextObserver {

                when (it) {
                    is Action.UPDATE -> {
                        router.toRoute.onNext(ToRoute.UPDATE())
                    }
                    is Action.SHOW_USER_LIST -> {
                        renderVm {
                            copy(isLoading = true)
                        }


                        nearMeListInteractor
                            .loadUsers(it.chatId)
                            .ioScheduler()
                            .subscribeWith(object : NearMeObserver<List<CompoundUser>>(router as NearMeRouter) {
                                override fun onError(e: Throwable) {
                                    super.onError(e)
                                    router.changeView.onNext(ChangeViewRoute.UNEXPECTED_ERROR())
                                }

                                override fun onNext(next: List<CompoundUser>) {
                                    super.onNext(next)
                                    renderVm {
                                        copy(isLoading = false, users = next, hasError = false)
                                    }
                                }

                                override fun onComplete() {
                                    super.onComplete()
                                    router.changeView.onNext(ChangeViewRoute.UNEXPECTED_ERROR())
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
                                    router.changeView.onNext(ChangeViewRoute.NEAR_ME_LIST())
                                } else {
                                    router.changeView.onNext(ChangeViewRoute.NO_LOCATION())
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
                                val purchase = purchaseEvent.purchases!!.findLast { it.sku == sku }
                                profilePreferences.saveHasSearchPurchase(true)
                                api.createPurchase(purchase!!.sku, purchase.orderId)
                            }
                            .ioScheduler()
                            .subscribeWith(
                                object : NearMeObserver<Any>(router as NearMeRouter) {
                                    override fun onNext(next: Any) {
                                        super.onNext(next)
                                        if (profilePreferences.hasSearchPurchase()) {
                                            router.changeView.onNext(ChangeViewRoute.NEAR_ME_LIST())
                                        }
                                    }

                                    override fun onComplete() {
                                        super.onComplete()
                                        renderVm {
                                            copy(isLoading = true)
                                        }
                                    }

                                    override fun onError(e: Throwable) {
                                        super.onError(e)
                                        renderVm {
                                            copy(isLoading = true)
                                        }
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
                                    router.changeView.onNext(ChangeViewRoute.NEAR_ME_LIST())
                                }
                            })
                            .bindPresenter(this)
                    }

                    is Action.CONSUME -> {
                        val sku = it.sku
                        purchaceInteractor
                            .consume(sku)
                            .timeout(20, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(NextObserver {
                                profilePreferences.saveHasSearchPurchase(false)
                                Log.d(TAG, "Consuming completed. Sku:$sku Code:$it")
                            })
                            .bindPresenter(this)

                    }
                }
            })
            .bindPresenter(this)


        router.inRoute
            .subscribeWith(NextObserver {
                when (it) {
                    is InRoute.UPDATE_NEEDED -> {
                        activity.presentFragment(NeedUpdateFragment.create(), true, true)
                    }
                }
            })
            .bindPresenter(this)
    }


    override fun start() {


    }


}