package io.wexchain.digitalwallet.api.domain

/**
 * Created by sisel on 2018/1/17.
 */
data class EtherScanResult<out T>(
        val status: Int,
        val message: String,
        val result: T
) {

}