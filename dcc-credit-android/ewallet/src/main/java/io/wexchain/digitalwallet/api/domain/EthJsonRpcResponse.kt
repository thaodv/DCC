package io.wexchain.digitalwallet.api.domain

/**
 * Created by sisel on 2018/1/18.
 */
data class EthJsonRpcResponse<T>(
        val id: Long,
        val jsonrpc: String,
        val result: T?,
        val error: Error?
) {
    data class Error(
            val code: Int,
            val message: String?
    )

    companion object {
        fun <T> default(): EthJsonRpcResponse<T> {
            return EthJsonRpcResponse(1, "2.0", null as T?, null)
        }
    }

}