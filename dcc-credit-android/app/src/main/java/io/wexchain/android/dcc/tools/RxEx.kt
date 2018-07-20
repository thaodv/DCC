package io.wexchain.android.dcc.tools

import android.app.Activity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.wexchain.dccchainservice.domain.Result


fun <T1, T2> pair(): BiFunction<T1, T2, Pair<T1, T2>> {
    return BiFunction { t1, t2 -> Pair(t1, t2) }
}

fun <T> Single<Result<T>>.check(): Single<T> {
    return this.compose(Result.checked())
            .observeOn(AndroidSchedulers.mainThread())
}

fun Activity.RequestPermission(vararg permissions: String): Observable<String> {
    return RxPermissions(this).request(*permissions).flatMap {
        if (it) {
            Observable.just("")
        } else {
            Observable.error(Throwable())
        }
    }.observeOn(AndroidSchedulers.mainThread())

}

