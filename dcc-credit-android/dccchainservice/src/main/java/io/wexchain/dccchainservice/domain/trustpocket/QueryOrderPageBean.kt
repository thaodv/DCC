package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/15 22:42.
 * usage:
 */
data class QueryOrderPageBean(
        @SerializedName("status") val status: Status,
        @SerializedName("assetValue") val assetValue: AssetValueBean,
        @SerializedName("requestIdentity") val requestIdentity: RequestIdentity,
        @SerializedName("createdTime") val createdTime: Long,
        @SerializedName("kind") val kind: String //  TRANSFER-IN TRANSFER-OUT WITHDRAW DEPOSIT
) {

    enum class Status {
        SUCCESS, //("成功")
        FAILED, //("失败")
        PROCESSING //("处理中")
    }

    data class RequestIdentity(
            @SerializedName("sourceCode") val sourceCode: String,
            @SerializedName("requestNo") val requestNo: String
    )

    data class AssetValueBean(
            @SerializedName("assetCode") val sourceCode: String,
            @SerializedName("amount") val requestNo: String
    )
}
