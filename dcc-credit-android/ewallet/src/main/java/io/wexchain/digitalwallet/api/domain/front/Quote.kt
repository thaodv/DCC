package io.wexchain.digitalwallet.api.domain.front

import io.wexchain.digitalwallet.util.toBigDecimalSafe
import java.math.RoundingMode

/**
 * Created by sisel on 2018/1/23.
 */

data class Quote(
        @JvmField val varietyCode: String,
        @JvmField val varietyName: String,
        @JvmField val sourceCode: String?,
        @JvmField val sourceName: String,
        @JvmField val quoteSymbol: String?,
        @JvmField val currencySymbol: String,
        @JvmField val scale: Int,
        @JvmField val open: String?,
        @JvmField val close: String?,
        @JvmField val high: String?,
        @JvmField val low: String?,
        @JvmField val amount: String?,
        @JvmField val volume: String?,
        @JvmField val price: String?,
        @JvmField val priceTime: Long?
) {
    fun expires(): Boolean {
        return priceTime == null || System.currentTimeMillis() - priceTime > 60 * 60 * 1000L
    }

    @Transient
    var change: String? = null

    @Transient
    var changeRate: String? = null

    fun computeChange() {
        val (v, r) = if (price != null && close != null) {
            val closeV = close.toBigDecimalSafe()
            val cv = price.toBigDecimalSafe() - closeV
            val cr = cv.divide(closeV, RoundingMode.HALF_EVEN)
            cv to cr
        } else null to null
        this.change = v?.setScale(4, RoundingMode.DOWN)?.toPlainString()
        this.changeRate = r?.let { it.scaleByPowerOfTen(2).setScale(2, RoundingMode.HALF_EVEN).toPlainString() + "%" }
    }

}