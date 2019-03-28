package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/15 21:29.
 * usage:
 */
data class QueryTransferOrderPageBean(
        @SerializedName("requestIdentity") val requestIdentity: RequestIdentityBean,
        @SerializedName("assetValue") val assetValue: AssetValueBean,
        @SerializedName("status") val status: Status
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

    data class RequestIdentityBean(
            @SerializedName("sourceCode") val sourceCode: String,
            @SerializedName("requestNo") val requestNo: String
    )

    data class AssetValueBean(
            @SerializedName("assetCode") val sourceCode: String,
            @SerializedName("amount") val requestNo: String
    )
}
