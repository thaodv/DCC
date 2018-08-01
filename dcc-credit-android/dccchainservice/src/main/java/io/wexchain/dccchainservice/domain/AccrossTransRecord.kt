package io.wexchain.dccchainservice.domain

import com.google.gson.annotations.SerializedName

/**
 * Created by sisel on 2018/3/17.
 */
/**
 *
 * @param id 订单id
 * @param originAssetCode 转出方
 * @param originAmount 转账金额
 * @param destAssetCode 转入方
 * @param destAmount 到账金额
 * @param feeAmount 手续费
 * @param status ACCEPTED ->"转移中 "DELIVERED ->"已完成"
 * @param createdTime 订单创建时间
 *
 */
data class AccrossTransRecord(
        @SerializedName("id") val id: Long,
        @SerializedName("originAssetCode") val originAssetCode: String,
        @SerializedName("originAmount") val originAmount: String,
        @SerializedName("destAssetCode") val destAssetCode: String,
        @SerializedName("destAmount") val destAmount: String,
        @SerializedName("feeAmount") val feeAmount: String,
        @SerializedName("status") val status: Status,
        @SerializedName("createdTime") val createdTime: Long?
) {

    enum class Status {
        ACCEPTED,//"转移中"

        DELIVERED; //"已完成"
    }

    fun isPublic2Private(): Boolean {
        return originAssetCode == "DCC" && destAssetCode == "DCC_JUZIX"
    }

    fun isPrivate2Public(): Boolean {
        return originAssetCode == "DCC_JUZIX" && destAssetCode == "DCC"
    }

}
