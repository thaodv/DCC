package io.wexchain.dccchainservice.domain

import io.wexchain.dccchainservice.type.TnOrderStatus

/**
 *Created by liuyang on 2018/12/5.
 */
data class TnLoanOrder(
        val id: Long,
        val chainOrderId: Int,
        val address: String,
        val failCode: Any,
        val failMessage: Any,
        val status: TnOrderStatus?,
        val repaymentTime: Long,
        val createdTime: Long,
        val orderInfoMap: OrderInfoMap,
        val loanOrder: LoanOrder,
        val auditTime: String,
        val remark: String,
        val approveAmount: Long,
        val approveDuration: Int,
        val approveDurationUnit: String,
        val canLoanTime: String
) {

    data class LoanOrder(
            val id: Int,
            val chainOrderId: Int,
            val address: String,
            val failCode: Any,
            val failMessage: Any,
            val status: String,
            val repaymentTime: String,
            val createdTime: String,
            val orderInfoMap: OrderInfoMap
    )

    data class OrderInfoMap(
            val orderInfo1: String,
            val orderInfo3: String,
            val orderInfo2: String
    )
}
