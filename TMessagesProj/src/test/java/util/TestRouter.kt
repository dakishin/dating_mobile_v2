package util

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

/**
 *   Created by dakishin@gmail.com
 */
 class TestRouter<I, O>(override val bag: CompositeDisposable) : com.dating.viper.Router<I, O> {
    override val toRoute = PublishSubject.create<I>()
    override val inRoute = PublishSubject.create<O>()

}