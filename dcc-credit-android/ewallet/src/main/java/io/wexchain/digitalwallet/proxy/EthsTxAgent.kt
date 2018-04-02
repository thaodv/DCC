package io.wexchain.digitalwallet.proxy

import io.reactivex.Single
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.api.EtherScanApi
import io.wexchain.digitalwallet.api.EthplorerApi

/**
 * Created by sisel on 2018/2/7.
 * eths standard tx history agent
 */
interface EthsTxAgent {
    /**
     * @param address the address queried
     * @param start block
     * @param end
     */
    fun listTransactionsOf(address: String, start: Long, end: Long): Single<List<EthsTransaction>>

    companion object {

        val NOT_SUPPORTED_YET = object : EthsTxAgent {
            override fun listTransactionsOf(address: String, start: Long, end: Long): Single<List<EthsTransaction>> {
                return Single.just(emptyList())
            }
        }

        /**
         * return a agent of ethereum
         */
        fun by(etherScanApi: EtherScanApi): EthsTxAgent {
            return object : EthsTxAgent {
                override fun listTransactionsOf(address: String, start: Long, end: Long): Single<List<EthsTransaction>> {
                    return etherScanApi
                            .getTransactions(address, start, end, "desc")
                            .map {
                                it.result.map { it.toDigitalTransaction() }
                            }
                }
            }
        }

        /**
         * return a agent of erc20
         */
        fun by(ethplorerApi: EthplorerApi, contractAddress: String): EthsTxAgent {
            return object : EthsTxAgent {
                override fun listTransactionsOf(address: String, start: Long, end: Long): Single<List<EthsTransaction>> {
                    return ethplorerApi.getAddressHistory(
                            address = address,
                            token = contractAddress
                    ).map {
                        it.operations.map {
                            it.toEthsTransaction()
                        }
                    }
                }
            }
        }
    }
}