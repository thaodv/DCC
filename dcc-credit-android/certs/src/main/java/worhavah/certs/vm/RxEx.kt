package worhavah.certs.vm

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.dccchainservice.domain.Result

fun <T> Single<Result<T>>.checkonMain(): Single<T> {
    return this.compose(Result.checked()).doMain()
}

fun <T> Single<T>.doMain(): Single<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}


