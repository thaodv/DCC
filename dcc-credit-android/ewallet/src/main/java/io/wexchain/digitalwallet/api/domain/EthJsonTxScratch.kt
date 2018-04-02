package io.wexchain.digitalwallet.api.domain

/**
 * Created by sisel on 2018/1/18.
 * eth request tx scratch
 */
data class EthJsonTxScratch(
        val from: String? = null,
        val to: String,
        val gas: String? = null,
        val gasPrice: String? = null,
        val value: String? = null,
        val data: String? = null
) {
}


