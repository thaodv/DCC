package io.wexchain.digitalwallet.api.domain

import java.math.BigInteger

/**
 * Created by sisel on 2018/1/17.
 */
data class EtherScanAccountBalance(
        val account: String,
        val balance: BigInteger
) {
}