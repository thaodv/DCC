package io.wexchain.dccchainservice.domain

/**
 * @author Created by Wangpeng on 2018/7/26 15:25.
 * usage:
 */
/**
 * @param serviceCharge 手续费
 * @param minAmount 最小转账金额
 * @param middleAddress 中间人地址
 */
data class ExchangeCondition(
        val serviceCharge: String,
        val minAmount: String,
        val middleAddress: String
)
