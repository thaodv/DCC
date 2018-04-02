package io.wexchain.digitalwallet.api.domain.front

import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by sisel on 2018/1/23.
 */
data class CResult<out T>(
        @JvmField val systemCode: String,
        @JvmField val businessCode: String?,
        @JvmField val data: T?,
        @JvmField val trace: String?,
        @JvmField val message: String?
) {
    val isSuccess: Boolean
        get() {
            return systemCode == SUCCESS && businessCode == SUCCESS
        }

    fun asError(): Exception {
//        return WexChainException(message, systemCode, businessCode)
        return IllegalStateException(message)
    }

    companion object {
        const val SUCCESS = "SUCCESS"

        fun <T> checkedAllowingNull(whenNull: T, applySchedules: Boolean = false): SingleTransformer<CResult<T>, T> {
            return SingleTransformer { upstream ->
                val checked = upstream
                        .map { resp ->
                            if (resp.isSuccess) {
                                resp.data ?: whenNull
                            } else {
                                throw resp.asError()
                            }
                        }
                return@SingleTransformer if (applySchedules) {
                    checked.subscribeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                } else checked
            }
        }

        fun <T> checked(applySchedules: Boolean = true): SingleTransformer<CResult<T>, T> {
            return SingleTransformer { upstream ->
                val checked = upstream
                        .flatMap { resp ->
                            if (resp.isSuccess && resp.data != null) {
                                return@flatMap Single.just(resp.data)
                            } else {
                                return@flatMap Single.error<T>(resp.asError())
                            }
                        }
                return@SingleTransformer if (applySchedules) {
                    checked.subscribeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                } else checked
            }
        }
    }
}