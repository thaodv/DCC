package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/15 22:42.
 * usage:
 */
data class QueryOrderPageBean(
        @SerializedName("id") val id: String,
        @SerializedName("status") val status: Status,
        @SerializedName("assetValue") val assetValue: AssetValueBean,
        @SerializedName("requestIdentity") val requestIdentity: RequestIdentity,
        @SerializedName("createdTime") val createdTime: Long,
        @SerializedName("type") val type: String,
        @SerializedName("kind") val kind: String //  TRANSFER-IN TRANSFER-OUT WITHDRAW DEPOSIT
) {

    enum class Status {
        SUCCESS, //("成功")
        FAILED, //("失败")
        PROCESSING, //("处理中")
        PAID, //已付款
        CANCELD, //已取消
        CLOSED, //已关闭
        ACCEPTED, //已接受
        REJECTED, //已拒绝
        FAILURE //失败
    }

    data class RequestIdentity(
            @SerializedName("sourceCode") val sourceCode: String,
            @SerializedName("requestNo") val requestNo: String
    )

    data class AssetValueBean(
            @SerializedName("assetCode") val assetCode: String,
            @SerializedName("amount") val amount: String
    )
}
