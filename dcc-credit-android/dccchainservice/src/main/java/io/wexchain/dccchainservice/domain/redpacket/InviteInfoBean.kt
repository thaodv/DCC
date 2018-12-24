package io.wexchain.dccchainservice.domain.redpacket

import com.google.gson.annotations.SerializedName


/**
 * @author Created by Wangpeng on 2018/12/24 11:21.
 * usage:
 */
data class InviteInfoBean(
        @SerializedName("unionId") val unionId: String,
        @SerializedName("inviteCount") val inviteCount: String,
        @SerializedName("redPacketOrderId") val redPacketOrderId: String
)
