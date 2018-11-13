package io.wexchain.dccchainservice.domain

import io.wexchain.dccchainservice.type.MathType

/**
 *Created by liuyang on 2018/11/7.
 */
data class ChangeOrder(
    val id: String,
    val amount: Int,
    val direction: MathType,
    val memo: String,
    val parentId: String,
    val parentType: String,
    val playerId: String,
    val status: String,
    val createdTime: Long,
    val lastUpdatedTime: Long,
    val taskOrder:TaskOrder
){
    data class TaskOrder(val amount:Int,val direction:String,val memo:String)
}