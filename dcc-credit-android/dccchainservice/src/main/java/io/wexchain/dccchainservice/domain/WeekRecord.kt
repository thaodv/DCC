package io.wexchain.dccchainservice.domain

/**
 *Created by liuyang on 2018/11/6.
 */

data class WeekRecord(
    val id: Int,
    val incrementValue: Int,
    val orderId: Int,
    val playerId: Int,
    val time: Long,
    val weekStartTime: Long,
    val createdTime: Long
)