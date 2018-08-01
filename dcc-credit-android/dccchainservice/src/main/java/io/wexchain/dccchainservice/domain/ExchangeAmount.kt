package io.wexchain.dccchainservice.domain

/**
 * @author Created by Wangpeng on 2018/7/26 15:25.
 * usage:
 */
/**
 * @param toPublicAmount 转到公链数量
 * @param toPrivateAmount 转到私链数量
 */
data class ExchangeAmount(
        val toPublicAmount: String,
        val toPrivateAmount: String
)
