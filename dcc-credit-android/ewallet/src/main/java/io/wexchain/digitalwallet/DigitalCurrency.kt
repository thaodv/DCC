package io.wexchain.digitalwallet

import io.wexchain.digitalwallet.util.hexToBigIntegerSafe
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by sisel on 2018/1/17.
 * Digital currency on block chain
 */
data class DigitalCurrency(
        @JvmField val symbol: String,
        @JvmField val chain: Chain,
        @JvmField val decimals: Int,
        @JvmField val description: String,
        @JvmField val icon: String?,
        /**
         * non-null for ERC20 tokens
         * null for block chain currencies
         */
        @JvmField val contractAddress: String?
) : Serializable {
    fun toIntExact(value: BigDecimal): BigInteger {
        return value.scaleByPowerOfTen(decimals).toBigIntegerExact()
    }

    fun toDecimalAmount(value: BigInteger): BigDecimal {
        return value.toBigDecimal().scaleByPowerOfTen(-decimals)
    }

    fun id(): Long {
        val ca = contractAddress.hexToBigIntegerSafe().hashCode().toLong()
        return (chain.hashCode().toLong() shl 32) or ca
    }

}