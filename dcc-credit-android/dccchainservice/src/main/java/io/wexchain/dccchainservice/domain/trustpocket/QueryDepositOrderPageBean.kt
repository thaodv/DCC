package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/11 16:53.
 * usage: 充值订单
 */
data class QueryDepositOrderPageBean(
        @SerializedName("amount") val amount: AmountBean,
        @SerializedName("mobileUserId") val mobileUserId: String,
        @SerializedName("status") val status: Status,
        @SerializedName("id") val id: String,
        @SerializedName("assetCode") val assetCode: String,
        @SerializedName("depositWalletAddress") val depositWalletAddress: String,
        @SerializedName("createdTime") val createdTime: Long

) {

    data class AmountBean(
            @SerializedName("decimalValue") val decimalValue: String
    )

    enum class Status {
        ACCEPTED,//"已接受"
        REJECTED, //"已拒绝"
        SUCCESS, //"成功"
        FAILURE //"失败"
    }
}
