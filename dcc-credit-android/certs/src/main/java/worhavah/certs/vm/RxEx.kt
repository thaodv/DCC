package worhavah.certs.vm

import android.app.Activity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.dcc.tools.RoomHelper
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

