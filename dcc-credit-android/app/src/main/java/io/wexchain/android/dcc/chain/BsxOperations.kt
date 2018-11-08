package io.wexchain.android.dcc.chain

import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.network.IpfsApi
import io.wexchain.android.dcc.network.sendRawTransaction
import io.wexchain.android.dcc.network.transactionReceipt
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.dcc.tools.check
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.api.domain.EthJsonRpcRequestBody
import io.wexchain.digitalwallet.api.domain.EthJsonRpcResponse
import io.wexchain.digitalwallet.api.domain.EthJsonTxReceipt
import io.wexchain.digitalwallet.api.domain.EthJsonTxScratch
import java.math.BigInteger

/**
 *Created by liuyang on 2018/8/21.
 */
object BsxOperations {

    private val passport by lazy {
        App.get().passportRepository
    }

    fun getBsxStatus(business: String): Single<EthJsonRpcResponse<String>> {
        return getBsxAddress(business).getBsxStatusApi(business)
    }

    fun getBsxSaleInfo(business: String): Single<EthJsonRpcResponse<String>> {
        return getBsxAddress(business).getBsxSaleInfoApi(business)
    }

    fun getBsxMinAmountPerHand(business: String): Single<EthJsonRpcResponse<String>> {
        return getBsxAddress(business).getBsxMinAmountPerHandApi(business)
    }

    fun getBsxInvestCeilAmount(business: String): Single<EthJsonRpcResponse<String>> {
        return getBsxAddress(business).getBsxInvestCeilAmountApi(business)
    }

    fun investedBsxTotalAmount(business: String): Single<EthJsonRpcResponse<String>> {
        return getBsxAddress(business).investedBsxTotalAmountApi(business)
    }

    fun investedBsxAmountMapping(business: String): Single<EthJsonRpcResponse<String>> {
        return getBsxAddress(business).investedBsxAmountMappingApi(business)
    }

    fun investBsx(business: String, amount: BigInteger, chain: Chain): Single<String> {
        return Singles.zip(
                getNonce(chain),
                getBsxAddress(business))
                .map {
                    val ipfsKey = Erc20Helper.investBsx(amount)
                    ipfsKey.txSigned(passport.getCurrentPassport()!!.credential, it.second, it.first)
                }
                .sendRawTransaction(business)
    }

    private fun Single<String>.getBsxStatusApi(business: String): Single<EthJsonRpcResponse<String>> {
        return this.map {
            Erc20Helper.getBsxStatus(it)
        }.postData(business)
    }

    private fun Single<String>.getBsxSaleInfoApi(business: String): Single<EthJsonRpcResponse<String>> {
        return this.map {
            Erc20Helper.getBsxSaleInfo(it)
        }.postData(business)
    }

    private fun Single<String>.getBsxMinAmountPerHandApi(business: String): Single<EthJsonRpcResponse<String>> {
        return this.map {
            Erc20Helper.getBsxMinAmountPerHand(it)
        }.postData(business)
    }

    private fun Single<String>.getBsxInvestCeilAmountApi(business: String): Single<EthJsonRpcResponse<String>> {
        return this.map {
            Erc20Helper.getBsxInvestCeilAmount(it)
        }.postData(business)
    }

    private fun Single<String>.investedBsxTotalAmountApi(business: String): Single<EthJsonRpcResponse<String>> {
        return this.map {
            Erc20Helper.investedBsxTotalAmount(it)
        }.postData(business)
    }

    private fun Single<String>.investedBsxAmountMappingApi(business: String): Single<EthJsonRpcResponse<String>> {
        return this.map {
            Erc20Helper.investedBsxAmountMapping(it, passport.getCurrentPassport()!!.address)
        }.postData(business)
    }


    private fun Single<String>.sendRawTransaction(business: String): Single<String> {
        return this.flatMap {
            App.get().contractApi.sendRawTransaction(business, it)
        }
    }

    private fun Single<String>.transactionReceipt(business: String): Single<EthJsonTxReceipt> {
        return this.flatMap {
            App.get().contractApi.transactionReceipt(business, it).retryWhen(RetryWithDelay.createGrowth(10, 2000))
        }
    }

    private fun Single<EthJsonTxScratch>.postData(business: String): Single<EthJsonRpcResponse<String>> {
        return this.flatMap {
            App.get().contractApi
                    .postCall(
                            business,
                            EthJsonRpcRequestBody(
                                    method = "eth_call",
                                    params = listOf(it, "latest"),
                                    id = IpfsApi.idAtomic.incrementAndGet()
                            )
                    )
        }
    }

    /**
     * Chain.JUZIX_PRIVATE - 私链
     * Chain.publicEthChain - 公链
     */
    fun getNonce(chain: Chain): Single<BigInteger> {
        val dccJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == chain }
        val agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccJuzix)
        return agent.getNonce(passport.getCurrentPassport()!!.address)
    }

    /**
     * 获取合约地址
     */
    private fun getBsxAddress(business: String): Single<String> {
        return App.get().contractApi.getIpfsContractAddress(business).check()
    }

}
