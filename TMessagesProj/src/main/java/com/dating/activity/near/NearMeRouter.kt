package com.dating.activity.near

import android.app.Activity
import com.dating.viper.Router
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

/**
 *   Created by dakishin@gmail.com
 */


sealed class Route {
    class NEAR_ME_LIST : Route()

}

sealed class Output {

}


class NearMeRouter(val activity: Activity,override val bag: CompositeDisposable) : Router<Route, Output> {


    override val route: PublishSubject<Route> = PublishSubject.create<Route>()
    override val output: PublishSubject<Output> = PublishSubject.create<Output>()
}