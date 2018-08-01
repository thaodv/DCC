package io.wexchain.dccchainservice.domain

/**
 * @author Created by Wangpeng on 2018/7/26 15:25.
 * usage:
 */
/**
 * @param fixedFeeAmount 手续费
 * @param minAmount 最小转账金额
 * @param originReceiverAddress 中间人地址
 */
data class ExchangeCondition(
        val fixedFeeAmount: String,
        val minAmount: String,
        val originReceiverAddress: String
)
