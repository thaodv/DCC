package io.wexchain.android.dcc.chain

import io.reactivex.Single
import io.reactivex.Flowable
import io.reactivex.SingleTransformer
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.ScfApi
import io.wexchain.dccchainservice.domain.BusinessCodes
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import java.security.PrivateKey

object ScfOperations {

    /**
     * check scf access token and proceed action
     * 1. check token existence
     * 2. on token fail retry after login(get new token)
     */
    fun <T> withScfToken(address: String?, privateKey: PrivateKey?): SingleTransformer<T, T> {
        return SingleTransformer {
            val call = it
            if (App.get().scfTokenManager.scfToken == null) {
                loginWithPassport(address, privateKey)
                        .flatMap { call }
            } else {
                it.retryWhen {
                    it.flatMap {
                        if (isTokenFail(it.cause?:it)) {
                            loginWithPassport(address, privateKey)
                        } else {
                            Single.error<T>(it)
                        }.toFlowable()
                    }
                }
            }
        }
    }

    fun <T> withScfTokenInCurrentPassport(): SingleTransformer<T, T> {
        val passport = App.get().passportRepository.getCurrentPassport()
        return withScfToken<T>(passport?.address, passport?.authKey?.getPrivateKey())
    }

    val currentToken = Single.fromCallable{ App.get().scfTokenManager.scfToken }

    private fun isTokenFail(e: Throwable): Boolean {
        return e is DccChainServiceException && e.businessCode == BusinessCodes.TOKEN_FORBIDDEN
    }

    private fun loginWithPassport(address: String?, privateKey: PrivateKey?): Single<String> {
        val app = App.get()
        val scfApi = App.get().scfApi
        if (address == null || privateKey == null) {
            return Single.error<String>(IllegalStateException())
        } else {
            return scfApi
                    .getNonce()
                    .compose(Result.checked())
                    .flatMap {
                        scfApi
                                .login(
                                        nonce = it,
                                        address = address,
                                        username = address,
                                        password = null,
                                        sign = ParamSignatureUtil.sign(privateKey, mapOf(
                                                "nonce" to it,
                                                "address" to address,
                                                "username" to address,
                                                "password" to null
                                        ))
                                )
                                .map {
                                    val body = it.body()
                                    if (it.isSuccessful && body != null && body.isSuccess) {
                                        it.headers()[ScfApi.HEADER_TOKEN]!!
                                    } else {
                                        throw IllegalStateException()
                                    }
                                }
                                .doOnSuccess {
                                    app.scfTokenManager.scfToken = it
                                }
                    }
        }
    }
}