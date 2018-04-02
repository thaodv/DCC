package io.wexchain.digitalwallet.api.domain

/**
 * Created by sisel on 2018/1/25.
 */
data class EthJsonTxInfo(
        val hash: String,
        val nonce: String,
        val blockHash: String,
        val blockNumber: String,
        val transactionIndex: String,
        val from: String,
        val to: String,
        val value: String,
        val gas: String,
        val gasPrice: String,
        val input: String?
)