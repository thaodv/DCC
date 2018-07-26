package io.wexchain.dccchainservice.domain

import com.google.gson.annotations.SerializedName

/**
 * Created by sisel on 2018/3/17.
 */
/**
 * @param amount 金额
 * @param status ACCEPTED ->"转移中 "DELIVERED ->"已完成"
 * @param exchangeTime 交易时间
 * @param serviceCharge
 * @param fromAssetCode 转出方
 * @param toAssetCode 转入方
 */
data class AccrossTransRecord(
        @SerializedName("amount") val amount: String,
        @SerializedName("status") val status: Status,
        @SerializedName("exchangeTime") val exchangeTime: Long?,
        @SerializedName("serviceCharge") val serviceCharge: String,
        @SerializedName("fromAssetCode") val fromAssetCode: String,
        @SerializedName("toAssetCode") val toAssetCode: String
) {

    enum class Status {
        ACCEPTED,//"转移中"

        DELIVERED; //"已完成"
    }

    fun isPublic2Private(): Boolean {
        return fromAssetCode == "DCC" && toAssetCode == "DCC_JUZIX"
    }

    fun isPrivate2Public(): Boolean {
        return fromAssetCode == "DCC_JUZIX" && toAssetCode == "DCC"
    }

}
