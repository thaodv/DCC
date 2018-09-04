package io.wexchain.android.dcc.chain

import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.wexchain.android.common.toHex
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
object IpfsOperations {

    private val passport by lazy {
        App.get().passportRepository
    }

    private val CIPHER = "AES256"
    val VERSION = BigInteger("1")

    fun checkPsw(): Single<String> {
        return getIpfsKey()
                .map {
                    val result = it.result as String
                    if (result.length > 64) result.substring(result.length - 64, result.length) else ""
                }
    }

    fun putIpfsKey(psw: String): Single<Boolean> {
        val sha265Key = passport.createIpfsKey(psw)
        val aesKey = passport.createIpfsAESKey(psw).toHex()
        val address = getIpfsAddress(IpfsApi.IPFS_KEY_HASH).blockingGet()
        return getNonce()
                .map {
                    val ipfsKey = Erc20Helper.putIpfsKey(sha265Key)
                    ipfsKey.txSigned(passport.getCurrentPassport()!!.credential, address, it)
                }
                .sendRawTransaction(IpfsApi.IPFS_KEY_HASH)
                .transactionReceipt(IpfsApi.IPFS_KEY_HASH)
                .map {
                    address
                }
                .getIpfsKeyApi()
                .checkToken()
                .doOnSuccess {
                    if (it) {
                        passport.setIpfsKeyHash(sha265Key.toHex())
                        passport.setIpfsAESKey(aesKey)
                    }
                }
    }

    fun putIpfsToken(business: String, token: String, digest1: ByteArray, digest2: ByteArray, nonce: BigInteger): Single<EthJsonTxReceipt> {
        return Singles.zip(
                getCertAddress(business),
                getIpfsAddress(IpfsApi.IPFS_METADATA))
                .map {
                    val putIpfsToken = Erc20Helper.putIpfsToken(it.first, VERSION, CIPHER, nonce.toByteArray(), token, digest1, digest2)
                    putIpfsToken.txSigned(passport.getCurrentPassport()!!.credential, it.second, nonce)
                }
                .sendRawTransaction(IpfsApi.IPFS_METADATA)
                .transactionReceipt(IpfsApi.IPFS_METADATA)
    }

    fun deleteIpfsToken(business: String): Single<EthJsonTxReceipt> {
        return Singles.zip(
                getCertAddress(business),
                getNonce(),
                getIpfsAddress(IpfsApi.IPFS_METADATA))
                .map {
                    val putIpfsToken = Erc20Helper.deleteIpfsToken(it.first)
                    putIpfsToken.txSigned(passport.getCurrentPassport()!!.credential, it.third, it.second)
                }
                .sendRawTransaction(IpfsApi.IPFS_METADATA)
                .transactionReceipt(IpfsApi.IPFS_METADATA)
    }

    fun getIpfsToken(business: String): Single<EthJsonRpcResponse<String>> {
        return Singles.zip(
                getIpfsAddress(IpfsApi.IPFS_METADATA),
                getCertAddress(business))
                .map {
                    Erc20Helper.getIpfsToken(it.first, passport.getCurrentPassport()!!.address, it.second)
                }
                .postData(IpfsApi.IPFS_METADATA)
    }

    fun delectedIpfsKey(): Single<String> {
        return Singles.zip(
                getNonce(),
                getIpfsAddress(IpfsApi.IPFS_KEY_HASH))
                .map {
                    val ipfsKey = Erc20Helper.deleteIpfsKey()
                    ipfsKey.txSigned(passport.getCurrentPassport()!!.credential, it.second, it.first)
                }
                .sendRawTransaction(IpfsApi.IPFS_KEY_HASH)
                .doOnSuccess {
                    passport.setIpfsKeyHash("")
                    passport.setIpfsAESKey("")
                }
    }

    fun getIpfsKey(): Single<EthJsonRpcResponse<String>> {
        return getIpfsAddress(IpfsApi.IPFS_KEY_HASH)
                .getIpfsKeyApi()
    }

    fun Single<EthJsonRpcResponse<String>>.checkToken(): Single<Boolean> {
        return this.map {
            val result = it.result!!.toString()
            val split = result.split("20")
            split.size > 2
        }
    }

    fun Single<String>.getIpfsKeyApi(): Single<EthJsonRpcResponse<String>> {
        return this
                .map {
                    Erc20Helper.getIpfsKey(it, passport.getCurrentPassport()!!.address)
                }
                .postData(IpfsApi.IPFS_KEY_HASH)
    }

    fun Single<String>.sendRawTransaction(business: String): Single<String> {
        return this.flatMap {
            App.get().contractApi.sendRawTransaction(business, it)
        }
    }

    fun Single<String>.transactionReceipt(business: String): Single<EthJsonTxReceipt> {
        return this.flatMap {
            App.get().contractApi.transactionReceipt(business, it).retryWhen(RetryWithDelay.createGrowth(8, 1000))
        }
    }

    fun Single<EthJsonTxScratch>.postData(business: String): Single<EthJsonRpcResponse<String>> {
        return flatMap {
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

    fun getNonce(): Single<BigInteger> {
        val dccJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }
        val agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccJuzix)
        return agent.getNonce(passport.getCurrentPassport()!!.address)
    }

    fun getIpfsAddress(business: String): Single<String> {
        return App.get().contractApi.getIpfsContractAddress(business).check()
    }

    fun getCertAddress(business: String): Single<String> {
        return App.get().chainGateway.getCertContractAddress(business).check()
    }

}