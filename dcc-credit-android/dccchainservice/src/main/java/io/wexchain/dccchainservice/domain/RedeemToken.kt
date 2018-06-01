package io.wexchain.dccchainservice.domain

import java.math.BigInteger
import com.google.gson.annotations.SerializedName


data class RedeemToken(
    @SerializedName("id") val id: Long,
    @SerializedName("scenarioCode") val scenarioCode: String,
    @SerializedName("amount") val amount: BigInteger?,
    @SerializedName("receiverAddress") val receiverAddress: String,
    @SerializedName("status") val status: Status
){
    enum class Status(
        val description:String
    ){
        CREATED("已创建"),

        FAILURE("已失败"),

        DECIDED("已确定金额"),

        SUCCESS("成功");
    }
}
