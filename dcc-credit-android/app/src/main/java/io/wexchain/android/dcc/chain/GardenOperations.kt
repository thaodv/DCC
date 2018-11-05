package io.wexchain.android.dcc.chain

import io.reactivex.Single
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.LoginInfo
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import io.wexchain.ipfs.utils.doMain
import retrofit2.Response

/**
 *Created by liuyang on 2018/8/21.
 */
object GardenOperations {

    private val passport by lazy {
        App.get().passportRepository
    }

    fun loginWithCurrentPassport(): Single<Response<Result<LoginInfo>>> {
        val api = App.get().marketingApi
        val address = passport.currPassport.value?.address
        val privateKey = passport.getCurrentPassport()?.authKey?.getPrivateKey()
        return if (address == null || privateKey == null) {
            Single.error(Throwable("passport is null"))
        } else api.getNonce2()
                .check()
                .flatMap {
                    api.login(address, it, ParamSignatureUtil.sign(
                            privateKey, mapOf(
                            "nonce" to it,
                            "address" to address
                    )))
                }
                .map {
                    val body = it.body()
                    if (it.isSuccessful && body != null) {
                        if (body.isSuccess) {
                            it
                        } else {
                            throw body.asError()
                        }
                    } else {
                        throw IllegalStateException()
                    }
                }
                .doOnSuccess {
                    App.get().gardenTokenManager.gardenToken = it.headers()[MarketingApi.HEADER_TOKEN]!!
                }
                .doMain()
    }

    fun boundWechat(code: String): Single<String> {
        val address = passport.currPassport.value?.address
        val app = App.get()
        return if (address == null) {
            Single.error(Throwable("passport is null"))
        } else app.marketingApi.bound(app.gardenTokenManager.gardenToken, address, code).checkonMain()
    }


}
