package io.wexchain.dccchainservice.domain.payment

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/27 18:04.
 * usage:
 */
data class GetGoodsViewBean(
        @SerializedName("goods") val goods: GoodsBean,
        @SerializedName("todayStats") val todayStats: TodayStatsBean,
        @SerializedName("totalStats") val totalStats: TotalStatsBean
) {
    data class GoodsBean(
            @SerializedName("id") val id: String,
            @SerializedName("assetCode") val assetCode: String,
            @SerializedName("amount") val amount: String?,
            @SerializedName("name") val name: String,
            @SerializedName("description") val description: String,
            @SerializedName("expiredTime") val expiredTime: Long?,
            @SerializedName("status") val status: Status
    ) {
        enum class Status {
            ACTIVE, CLOSED
        }
    }

    data class TodayStatsBean(
            @SerializedName("orderNumber") val orderNumber: String,
            @SerializedName("orderAmount") val orderAmount: String
    )

    data class TotalStatsBean(
            @SerializedName("orderNumber") val orderNumber: String,
            @SerializedName("orderAmount") val orderAmount: String
    )
}
