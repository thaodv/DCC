package io.wexchain.digitalwallet

import java.math.BigDecimal

/**
 * Created by sisel on 2018/1/17.
 * Digital currency - usd/usdt quote
 */
data class DcQuote(
        /**
         * exchange in usd or usdt
         */
        val price: BigDecimal,
        val high: BigDecimal,
        val low: BigDecimal,
        /**
         * quote data source
         * eg: huobi.pro
         */
        val source: String
) {
}