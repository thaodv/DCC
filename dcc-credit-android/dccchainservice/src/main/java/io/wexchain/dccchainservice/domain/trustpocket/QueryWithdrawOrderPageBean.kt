package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/11 17:03.
 * usage:
 */
data class QueryWithdrawOrderPageBean(
        @SerializedName("id") val id: String,
        @SerializedName("assetCode") val assetCode: String,
        @SerializedName("status") val status: Status,
        @SerializedName("fee") val fee: FeeBean,
        @SerializedName("receiverAddress") val receiverAddress: String,
        @SerializedName("createdTime") val createdTime: Long,
        @SerializedName("requestIdentity") val requestIdentity: RequestIdentity,
        @SerializedName("amount") val amount: AmountBean

) {
    enum class Status {
        SUCCESS, //("成功")
        FAILED, //("失败")
        REJECTED,
        PROCESSING //("处理中")
    }

    data class FeeBean(
            @SerializedName("decimalValue") val decimalValue: String
    )

    data class RequestIdentity(
            @SerializedName("sourceCode") val sourceCode: String,
            @SerializedName("requestNo") val requestNo: String
    )

    data class AmountBean(
            @SerializedName("decimalValue") val decimalValue: String
    )
}
