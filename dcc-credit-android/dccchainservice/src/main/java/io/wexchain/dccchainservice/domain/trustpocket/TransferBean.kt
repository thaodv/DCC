package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/11 17:13.
 * usage:
 */
class TransferBean(
        @SerializedName("amount") val amount: AmountBean,
        @SerializedName("assetCode") val assetCode: String,
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

    data class AmountBean(
            @SerializedName("decimalValue") val decimalValue: String
    )

}
