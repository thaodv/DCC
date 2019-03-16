package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/15 21:33.
 * usage:
 */
data class GetTransferOrderBean(
        @SerializedName("id") val id: String,
        @SerializedName("createdTime") val createdTime: Long,
        @SerializedName("mobile") val mobile: String,
        @SerializedName("assetCode") val assetCode: String,
        @SerializedName("amount") val amount: AmountBean,
        @SerializedName("status") val status: Status
) {

    enum class Status {
        SUCCESS, //("成功")
        FAILED, //("失败")
        PROCESSING //("处理中")
    }

    data class AmountBean(
            @SerializedName("decimalValue") val decimalValue: String
    )
}
