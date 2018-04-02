package io.wexchain.digitalwallet.util

import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/22.
 */

fun parseBigDecimal(text: String, fallback: BigDecimal = BigDecimal.ZERO): BigDecimal {
    return try {
        BigDecimal(text)
    } catch (e: NumberFormatException) {
//        println("fail to decimal $text")
        fallback
    }
}

fun String.toBigDecimalSafe(fallback: BigDecimal = BigDecimal.ZERO) = parseBigDecimal(this, fallback)

fun parseBigInteger(text: String, fallback: BigInteger = BigInteger.ZERO): BigInteger {
    return try {
        BigInteger(text)
    } catch (e: NumberFormatException) {
//        println("fail to bigint $text")
        fallback
    }
}

fun String.toBigIntegerSafe(fallback: BigInteger = BigInteger.ZERO) = parseBigInteger(this, fallback)

fun String?.hexToBigIntegerSafe(fallback: BigInteger = BigInteger.ZERO) = parseBigIntegerHex(this, fallback)

fun parseBigIntegerHex(str: String?, fallback: BigInteger): BigInteger {
    return try {
        Numeric.toBigInt(str)
    } catch (e: Exception) {
        fallback
    }
}
