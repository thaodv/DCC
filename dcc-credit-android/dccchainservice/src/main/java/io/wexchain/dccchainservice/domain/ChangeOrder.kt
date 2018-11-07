package io.wexchain.dccchainservice.domain

/**
 *Created by liuyang on 2018/11/7.
 */
data class ChangeOrder(
    val id: Int,
    val amount: Int,
    val direction: String,
    val memo: String,
    val parentId: Any,
    val parentType: Any,
    val playerId: String,
    val status: String,
    val createdTime: Long,
    val lastUpdatedTime: Long
)