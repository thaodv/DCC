package io.wexchain.dccchainservice.domain.redpacket

import com.google.gson.annotations.SerializedName


/**
 * @author Created by Wangpeng on 2018/12/24 11:21.
 * usage:
 */
data class RedPacketBoundBean(
        @SerializedName("activity") val activity: RedPacketActivityBean,
        @SerializedName("inviteInfo") val inviteInfo: InviteInfoBean,
        @SerializedName("redPacketStocks") val redPacketStocks: List<QueryStoreBean>
)
