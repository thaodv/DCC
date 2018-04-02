package io.wexchain.digitalwallet.api.domain

data class EthJsonTxReceipt(
        val blockHash: String,
        val blockNumber: String,
        val contractAddress: Any,
        val cumulativeGasUsed: String,
        val from: String,
        val gasUsed: String,
        val logs: List<Any>,
        val logsBloom: String,
        val status: String,
        val to: String,
        val transactionHash: String,
        val transactionIndex: String
)