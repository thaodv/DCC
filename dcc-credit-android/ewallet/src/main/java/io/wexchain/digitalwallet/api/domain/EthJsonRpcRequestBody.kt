package io.wexchain.digitalwallet.api.domain

/**
 * Created by sisel on 2018/1/18.
 */
data class EthJsonRpcRequestBody<T>(
        val jsonrpc: String = "2.0",
        val method: String,
        val params: List<T>,
        val id: Long
)