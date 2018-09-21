package worhavah.regloginlib.tools

import android.content.Intent
import android.util.Log
import android.view.View

import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.ScfAccountInfo
import io.wexchain.dccchainservice.domain.TicketResponse
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import retrofit2.HttpException
import worhavah.regloginlib.Net.*
import worhavah.regloginlib.Passport
import worhavah.regloginlib.PassportRepository
import java.math.BigDecimal
import java.math.BigInteger
import java.security.PrivateKey

object ScfOperations {

    val LOAN_DIGEST_VERSION_1 = BigInteger.ONE
    val loadingEvent = SingleLiveEvent<Boolean>()
    private const val DIGEST = "SHA256"

    @Deprecated("replaced")
    fun cancelLoan(orderId: Long, passport: Passport): Single<String> {
        val credentials = passport.credential
        val nonce = privateChainNonce(credentials.address)
        val chainGateway = Networkutils.chainGateway
        return Single.just(orderId)
            .observeOn(Schedulers.computation())
            .map {
                EthsFunctions.cancelLoan(orderId)
            }
            .observeOn(Schedulers.io())
            .flatMap { cancelFunc ->
                chainGateway.getLoanContractAddress()
                    .compose(Result.checked())
                    .flatMap {
                        val tx = cancelFunc.txSigned(passport.credential, it, nonce)
                        chainGateway.cancelLoanOrder(tx)
                            .compose(Result.checked())
                    }
            }
            .confirmOnChain(chainGateway)
    }

    fun getScfAccountInfo(): Single<ScfAccountInfo> {
        val scfApi = Networkutils.scfApi
        val passport = Networkutils.passportRepository.getCurrentPassport()
        passport?.authKey ?: return Single.error(IllegalStateException())
        val address = passport.address
        val privateKey = passport.authKey.getPrivateKey()
        return scfApi.getNonce()
            .compose(Result.checked())
            .flatMap {
                scfApi
                    .getScfMemberInfo(
                        nonce = it,
                        address = address,
                        sign = io.wexchain.dccchainservice.util.ParamSignatureUtil.sign(
                            privateKey, mapOf(
                                "nonce" to it,
                                "address" to address
                            )
                        )
                    )
                    .compose(Result.checkedAllowingNull(ScfAccountInfo.ABSENT))
            }
    }


    fun Single<String>.confirmOnChain(api: ChainGateway): Single<String> {
        return this.flatMap { txHash ->
            api.getReceiptResult(txHash)
                .compose(Result.checked())
                .map {
                    if (!it.hasReceipt) {
                        throw DccChainServiceException("no receipt yet")
                    }
                    it
                }
                .retryWhen(RetryWithDelay.createSimple(6, 5000L))
                .map {
                    if (!it.approximatelySuccess) {
                        throw DccChainServiceException()
                    }
                    txHash
                }
        }
    }


    private fun BigDecimal.toLoanFormatString(): String {
        val s4 = this.setScale(4, java.math.RoundingMode.DOWN)
        return s4.toPlainString()
    }

    fun loadHolding(): Single<BigDecimal> {
        val passport = PassportRepository.getCurrentPassport()
        return if (passport != null) {
            val dccPrivate = MultiChainHelper.getDccPrivate()
            Networkutils.assetsRepository.getDigitalCurrencyAgent(dccPrivate)
                .getBalanceOf(passport.address)
                .map {
                    dccPrivate.toDecimalAmount(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
        } else {
            Single.error<BigDecimal>(IllegalStateException())
        }
    }


    /**
     * check scf access token and proceed action
     * 1. check token existence
     * 2. on token fail retry after login(get new token)
     */
    fun <T> withScfToken(address: String?, privateKey: PrivateKey?): SingleTransformer<T, T> {
        return SingleTransformer {
            val call = it
            if (Networkutils.scfTokenManager.scfToken == null) {
                loginWithPassport(address, privateKey)
                    .flatMap { call }
            } else {
                it.retryWhen {
                    it.flatMap {
                        if (isTokenFail(it.cause ?: it)) {
                            loginWithPassport(address, privateKey)

                        }
                      /*  else if (isTokenFail(it.cause ?: it)) {
                             loginWithPassport(address, privateKey)
                        } */
                        else {
                            if (isTokenFailILL(it.cause ?: it)) {
                               // loginWithPassport(address, privateKey)

                                Log.e("isTokenFailILL", "isTokenFailILLisTokenFailILLisTokenFailILLisTokenFailILLisTokenFailILLisTokenFailILLisTokenFailILLisTokenFailILL")
                                Single.error<T>(it)
                            }else{
                                Single.error<T>(it)
                            }
                        }.toFlowable()
                    }
                }
            }
        }
    }

    fun <T> withScfTokenInCurrentPassport(): SingleTransformer<T, T> {
        val passport = Networkutils.passportRepository.getCurrentPassport()
        return withScfToken<T>(passport?.address, passport?.authKey?.getPrivateKey())
    }

    inline fun <T> withScfTokenInCurrentPassport(
        allowNull: T? = null,
        crossinline call: (String) -> Single<Result<T>>
    ): Single<T> {
        return currentToken
            .flatMap {
                val check = if (allowNull == null) {
                    Result.checked<T>()
                } else {
                    Result.checkedAllowingNull(allowNull)
                }
                call(it).compose(check)
            }
            .compose(withScfTokenInCurrentPassport())
    }

    val currentToken = Single.fromCallable { Networkutils.scfTokenManager.scfToken }

    private fun isTokenFail(e: Throwable): Boolean {
        return e is DccChainServiceException && e.businessCode == BusinessCodes.TOKEN_FORBIDDEN
    }

    private fun isTokenFailILL(e: Throwable): Boolean {
        return e is DccChainServiceException && e.businessCode == BusinessCodes.TOKEN_FORBIDDEN
    }

    public fun loginWithPassport(address: String?, privateKey: PrivateKey?): Single<String> {
        val app = Networkutils.context
        val scfApi = Networkutils.scfApi
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
                            sign = ParamSignatureUtil.sign(
                                privateKey, mapOf(
                                    "nonce" to it,
                                    "address" to address,
                                    "username" to address,
                                    "password" to null
                                )
                            )
                        )
                        .map {
                            val body = it.body()
                            if (it.isSuccessful && body != null) {
                                if (body.isSuccess) {
                                    it.headers()[ScfApi.HEADER_TOKEN]!!
                                } else {

                                    if (body.businessCode.equals("ILLEGAL_SIGN")||body.businessCode.equals("SIGN_MESSAGE_INVALID")){
                                        Log.e("body", "bodybodybodybody")
                                        Networkutils.context!!.startActivity(Intent(Networkutils.context,ReLoginActivity::class.java) )
                                        /*AlertDialog(Networkutils.context!!).builder().setTitle("重新登陆")
                                            .setMsg("该钱包地址已在其他设备上登陆，请更新密钥以在本设备重新登陆")
                                            .setPositiveButton("更新密钥", View.OnClickListener {
                                                //Log.e("onItemClick", "确认退出确认退出确认退出确认退出确认退出")
                                                // showNOPermission()
                                                updateAuthKey(AuthKeyChangeRecord.UpdateType.UPDATE)
navigateTo(RuleActivity::class.java)
                                            }).setNegativeButton("取消", View.OnClickListener {
                                                Log.e("onItemClick", "取消取消取消取消取消取消取消")
                                            }).show()*/
                                    }

                                    throw body.asError()
                                }
                            } else {
                                throw IllegalStateException()
                            }
                        }
                        .doOnSuccess {
                            Networkutils.scfTokenManager.scfToken = it
                        }
                        .doOnError {
                            it.printStackTrace()
                            /*AlertDialog(Networkutils.context!!).builder().setTitle("重新登陆")
                                .setMsg("该钱包地址已在其他设备上登陆，请更新密钥以在本设备重新登陆")
                                .setPositiveButton("更新密钥", View.OnClickListener {
                                    //Log.e("onItemClick", "确认退出确认退出确认退出确认退出确认退出")
                                    // showNOPermission()


                                }).setNegativeButton("取消", View.OnClickListener {
                                    Log.e("onItemClick", "取消取消取消取消取消取消取消")
                                }).show()*/
                        }

                }
        }
    }
    public fun updateAuthKey( ) {
        val p = Networkutils.passportRepository.getCurrentPassport()
        Networkutils.chainGateway.getTicket()
            .compose(Result.checked())
            .flatMap {
                require(it.accessRestriction.name .equals("GRANTED"))
                PassportOperations.updatePubKeyAndUploadChecked(p!!, it.ticket, null)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                /* PassportRepository.addAuthKeyChangedRecord(
                     AuthKeyChangeRecord(p!!.address, System.currentTimeMillis(), updateType)
                 )*/
            }
            .doOnSubscribe {
                // loadingEvent.value = true
            }
            .doFinally {
                //  loadingEvent.value = false
            }
            .subscribe(
                {
                    //   authKeyChangeEvent.value = updateType
                },
                {
                    //  errorEvent.value = it
                }
            )
    }

    /*public fun updateAuthKey(updateType: AuthKeyChangeRecord.UpdateType) {
        val p = Networkutils.passportRepository.getCurrentPassport()
        Networkutils.chainGateway.getTicket()
            .compose(Result.checked())
            .flatMap {
                require(it.accessRestriction.name .equals("GRANTED"))
                PassportOperations.updatePubKeyAndUploadChecked(p!!, it.ticket, null)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
               *//* PassportRepository.addAuthKeyChangedRecord(
                    AuthKeyChangeRecord(p!!.address, System.currentTimeMillis(), updateType)
                )*//*
            }
            .doOnSubscribe {
                // loadingEvent.value = true
            }
            .doFinally {
                //  loadingEvent.value = false
            }
            .subscribe(
                {
                    //   authKeyChangeEvent.value = updateType
                },
                {
                    //  errorEvent.value = it
                }
            )
    }*/

    fun registerWithCurrentPassport(code: String?): Single<String> {
        val scfApi = Networkutils.scfApi
        val passport = PassportRepository.letinit(Networkutils.context!!).getCurrentPassport()
        passport?.authKey ?: return Single.error(IllegalStateException())
        val address = passport.address
        val privateKey = passport.authKey.getPrivateKey()
        return scfApi.getNonce()
            .compose(Result.checked())
            .flatMap {
                scfApi
                    .scfRegister(
                        nonce = it,
                        address = address,
                        loginName = address,
                        inviteCode = code,
                        sign = ParamSignatureUtil.sign(
                            privateKey, mapOf(
                                "nonce" to it,
                                "address" to address,
                                "loginName" to address,
                                "inviteCode" to code
                            )
                        )
                    )
                    .map {
                        if (it.isSuccessful) {
                            val result = it.body()!!
                            if (result.isSuccess) {
                                val token = it.headers()[ScfApi.HEADER_TOKEN]!!
                                Networkutils.scfTokenManager.scfToken = token
                            }
                            result
                        } else {
                            throw HttpException(it)
                        }
                    }
                    .compose(Result.checked())
            }
    }


}