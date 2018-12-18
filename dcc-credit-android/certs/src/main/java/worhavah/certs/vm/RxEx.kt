package worhavah.certs.vm

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
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


