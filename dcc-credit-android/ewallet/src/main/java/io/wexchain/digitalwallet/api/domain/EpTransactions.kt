package io.wexchain.digitalwallet.api.domain

import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction
import io.wexchain.digitalwallet.util.toBigIntegerSafe
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/25.
 */

data class EpTransactions(
        val operations: List<Operation>
) {
    data class Operation(
            val timestamp: Long,
            val transactionHash: String,
            val tokenInfo: TokenInfo,
            val type: String,
            val value: String,
            val from: String,
            val to: String//,
         //   val nonce: BigInteger
    ) {
        fun toEthsTransaction(): EthsTransaction {
            return EthsTransaction(
                    digitalCurrency = this.tokenInfo.toDigitalCurrency(),
                    txId = this.transactionHash,
                    from = this.from,
                    to = this.to,
                    amount = this.value.toBigIntegerSafe(),
                    transactionType = typeOfEthsTransaction(),
                    remarks = null,
                    time = this.timestamp * 1000L,
                    blockNumber = 0L,
                    gas = BigInteger.ZERO,
                    gasPrice = BigInteger.ZERO,
                    gasUsed = BigInteger.ZERO,
                    status = EthsTransaction.Status.MINED,
                            nonce=BigInteger.ZERO
            )
        }

        fun typeOfEthsTransaction(): String {
            return when (this.type) {
                "transfer" -> "转账"
                else -> ""
            }
        }
    }

    data class TokenInfo(
            val address: String,
            val name: String,
            val decimals: Int,
            val symbol: String,
            val totalSupply: String,
            val owner: String,
            val txsCount: Int,
            val transfersCount: Int,
            val lastUpdated: Int,
            val issuancesCount: Int,
            val holdersCount: Int
    ) {
        fun toDigitalCurrency(): DigitalCurrency {
            return DigitalCurrency(
                    symbol = this.symbol,
                    chain = Chain.publicEthChain,
                    decimals = this.decimals,
                    description = this.name,
                    icon = null,
                    contractAddress = this.address,
                    sort = 10000
            )
        }
    }
}
