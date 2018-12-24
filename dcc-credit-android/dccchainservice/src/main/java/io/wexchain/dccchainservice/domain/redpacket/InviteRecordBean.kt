package io.wexchain.dccchainservice.domain.redpacket

import com.google.gson.annotations.SerializedName


/**
 * @author Created by Wangpeng on 2018/12/24 11:21.
 * usage:
 */
data class InviteRecordBean(
        @SerializedName("inviterUnionId") val inviterUnionId: String,
        @SerializedName("nickName") val nickName: String,
        @SerializedName("portrait") val portrait: String,
        @SerializedName("unionId") val unionId: String,
        @SerializedName("createdTime") val createdTime: String
)
