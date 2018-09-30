package io.wexchain.android.common.base

import android.support.v4.app.Fragment
import io.reactivex.Single


abstract class BaseCompatFragment : Fragment() {

    fun <T> Single<T>.withLoading(): Single<T> {
        return this
                .doOnSubscribe {
                    (activity as BaseCompatActivity).showLoadingDialog()
                }.doFinally {
                    (activity as BaseCompatActivity). hideLoadingDialog()
                }
    }

}
