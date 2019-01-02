package io.wexchain.dccchainservice.domain.redpacket

import com.google.gson.annotations.SerializedName


/**
 * @author Created by Wangpeng on 2018/12/24 11:21.
 * usage:
 *
 * @param nickName 昵称
 * @param portrait 头像路径
 * @param createdTime 创建时间
 *
 */
data class InviteRecordBean(
        @SerializedName("nickName") val nickName: String,
        @SerializedName("portrait") val portrait: String,
        @SerializedName("createdTime") val createdTime: Long
)
