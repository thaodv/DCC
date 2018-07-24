package io.wexchain.android.dcc.tools

import android.app.Activity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.wexchain.dccchainservice.domain.Result


fun <T1, T2> pair(): BiFunction<T1, T2, Pair<T1, T2>> {
    return BiFunction { t1, t2 -> Pair(t1, t2) }
}

fun <T> Single<Result<T>>.check(): Single<T> {
    return this.compose(Result.checked())
}

fun <T> Single<Result<T>>.checkonMain(): Single<T> {
    return this.compose(Result.checked()).doMain()
}

fun <T> Single<T>.doMain(): Single<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.doRoom(): Single<T> {
    return this.observeOn(RoomHelper.roomScheduler)
}

fun <T> Single<T>.doBack(): Single<T> {
    return this.observeOn(Schedulers.io())
}

fun Activity.requestPermission(vararg permissions: String): Observable<Any> {
    return RxPermissions(this).request(*permissions).flatMap {
        if (it) {
            Observable.empty<Any>()
        } else {
            Observable.error(Throwable())
        }
    }.observeOn(AndroidSchedulers.mainThread())

}

