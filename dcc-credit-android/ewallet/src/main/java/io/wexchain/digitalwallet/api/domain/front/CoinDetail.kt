package io.wexchain.digitalwallet.api.domain.front

/**
 * @author Created by Wangpeng on 2018/7/25 13:30.
 * usage:
 */
/**
 * @param fullName 代币全名
 * @param percent_change_1h 代币一小时涨跌幅
 * @param percent_change_24h 代币24小时涨跌幅
 * @param percent_change_7d 代币7天涨跌幅
 * @param price 代币当前价格
 * @param price_change_24 代币24x小时价格浮动值
 * @param symbol 代币简称
 * @param timeStamp 最新行情时间
 * @param volume_24 代币最近24小时成交量
 */
data class CoinDetail(
        @JvmField val fullName: String,
        @JvmField val percent_change_1h: String,
        @JvmField val percent_change_24h: String,
        @JvmField val percent_change_7d: String,
        @JvmField val price: String,
        @JvmField val price_change_24: String,
        @JvmField val symbol: String,
        @JvmField val timeStamp: Long,
        @JvmField val volume_24: String) {

    fun expires(): Boolean {
        return timeStamp == null || System.currentTimeMillis() - timeStamp > 60 * 60 * 1000L
    }
}

