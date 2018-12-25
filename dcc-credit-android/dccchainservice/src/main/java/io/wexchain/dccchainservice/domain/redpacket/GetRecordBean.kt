package io.wexchain.dccchainservice.domain.redpacket

import com.google.gson.annotations.SerializedName


/**
 * @author Created by Wangpeng on 2018/12/24 11:21.
 * usage:
 *
 * @param nickName 昵称
 * @param amount 金额
 * @param receiveTime 领取红包时间
 *
 */
data class GetRecordBean(
        @SerializedName("amount") val amount: String,
        @SerializedName("nickName") val nickName: String,
        @SerializedName("receiveTime") val receiveTime: Long
)
