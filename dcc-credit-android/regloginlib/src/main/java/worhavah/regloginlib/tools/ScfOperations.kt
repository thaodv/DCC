package worhavah.regloginlib.tools

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.Result
import worhavah.regloginlib.Net.ChainGateway
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.Passport
import worhavah.regloginlib.PassportRepository
import java.math.BigDecimal

object ScfOperations {

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

}
