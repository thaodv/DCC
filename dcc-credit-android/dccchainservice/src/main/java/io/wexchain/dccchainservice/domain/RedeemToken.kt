package io.wexchain.dccchainservice.domain

import java.math.BigInteger

data class RedeemToken(
    val scenarioCode:String,
    val amount:BigInteger
) {
}