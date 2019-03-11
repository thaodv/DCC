package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/11 17:11.
 * usage:
 */
data class WithdrawBean(
        @SerializedName("amount") val amount: String,
        @SerializedName("mobileUserId") val mobileUserId: String,
        @SerializedName("status") val status: Status,
        @SerializedName("receiverAddress") val receiverAddress: String,
        @SerializedName("requestIdentity") val requestIdentity: RequestIdentity
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
}
