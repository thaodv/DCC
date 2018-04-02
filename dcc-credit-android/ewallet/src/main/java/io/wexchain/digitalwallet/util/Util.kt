package io.wexchain.digitalwallet.util

import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.regex.Pattern

/**
 * Created by sisel on 2018/1/22.
 */

val ethAddressPattern: Pattern = Pattern.compile("^(ethereum:)?(0x)?[0-9a-fA-F]{40}$")

fun isEthAddress(text: String): Boolean {
    return ethAddressPattern.matcher(text).matches()
}

fun extractStandardEthAddress(text: String): String? {
    return if (isEthAddress(text)) {
        "0x${text.substring(text.length - 40).toUpperCase()}"
    } else null
}

/**
 * @param price gas price in gwei
 * @param limit gas limit/used amount
 */
fun computeEthTxFee(price: BigDecimal, limit: BigInteger): BigDecimal {
    return (price.scaleByPowerOfTen(9) * limit.toBigDecimal()).scaleByPowerOfTen(-18).stripTrailingZeros()
}

private val g = BigDecimal("1000000000")

fun gweiTowei(value: BigDecimal) = (value * g).toBigInteger()

fun weiToGwei(value: BigInteger) = value.toBigDecimal().setScale(9, RoundingMode.UNNECESSARY).divide(g, RoundingMode.UNNECESSARY)


@Throws(NumberFormatException::class)
fun String.hexToBigIntegerOrThrow(): BigInteger {
    return if (this.equals("0x", true)) BigInteger.ZERO else Numeric.toBigInt(this)
}