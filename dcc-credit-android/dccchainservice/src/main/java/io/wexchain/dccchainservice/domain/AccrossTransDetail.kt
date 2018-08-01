package io.wexchain.dccchainservice.domain

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2018/7/30 16:47.
 * usage:
 */
/**
 * @param id 订单id
 * @param originAssetCode 转出方
 * @param originAmount 发送方金额
 * @param destAssetCode 目标资产代码
 * @param destAmount 接收方金额
 * @param feeAmount 手续费
 * @param status ACCEPTED ->"转移中 "DELIVERED ->"已完成"
 * @param createdTime 创建时间
 * @param lastUpdatedTime 更新时间
 * @param originReceiverAddress 源链中间地址
 * @param beneficiaryAddress 转出的付款地址/转入的接收地址
 * @param destPayerAddress 目标链付款地址
 * @param originTxHash 发送方交易号
 * @param destTxHash 接受方交易号
 * @param originBlockNumber 发送方区块高度
 * @param destBlockNumber 接受方区块高度
 * @param originTradeTime 发送方交易时间
 * @param destTradeTime 接受方交易时间
 */
data class AccrossTransDetail(
        @SerializedName("id") val id: Long,
        @SerializedName("originAssetCode") val originAssetCode: String,
        @SerializedName("originAmount") val originAmount: String,
        @SerializedName("destAssetCode") val destAssetCode: String,
        @SerializedName("destAmount") val destAmount: String,
        @SerializedName("feeAmount") val feeAmount: String,
        @SerializedName("status") val status: Status,
        @SerializedName("createdTime") val createdTime: Long,
        @SerializedName("lastUpdatedTime") val lastUpdatedTime: Long?,
        @SerializedName("originReceiverAddress") val originReceiverAddress: String,
        @SerializedName("beneficiaryAddress") val beneficiaryAddress: String,
        @SerializedName("destPayerAddress") val destPayerAddress: String,
        @SerializedName("originTxHash") val originTxHash: String,
        @SerializedName("destTxHash") val destTxHash: String,
        @SerializedName("originBlockNumber") val originBlockNumber: Long,
        @SerializedName("destBlockNumber") val destBlockNumber: Long,
        @SerializedName("originTradeTime") val originTradeTime: Long,
        @SerializedName("destTradeTime") val destTradeTime: Long

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
