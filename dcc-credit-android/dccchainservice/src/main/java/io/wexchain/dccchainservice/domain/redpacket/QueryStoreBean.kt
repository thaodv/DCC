package io.wexchain.dccchainservice.domain.redpacket

import com.google.gson.annotations.SerializedName


/**
 *
 * @author Created by Wangpeng on 2018/12/24 11:21.
 * usage:
 *
 * @param id ID
 * @param stockCount 剩余库存
 * @param count 总数
 * @param description 描述
 * @param amount 金额
 * @param exceptInviteCount 预期解锁邀请人数
 * @param needInviteCount 还需邀请人数
 *
 */
data class QueryStoreBean(
        @SerializedName("id") val id: String,
        @SerializedName("stockCount") val stockCount: String,
        @SerializedName("count") val count: String,
        @SerializedName("description") val description: String,
        @SerializedName("amount") val amount: String,
        @SerializedName("exceptInviteCount") val exceptInviteCount: String,
        @SerializedName("needInviteCount") val needInviteCount: String

)
