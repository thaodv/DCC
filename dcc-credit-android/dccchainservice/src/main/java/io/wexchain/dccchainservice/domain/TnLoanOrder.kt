package io.wexchain.dccchainservice.domain

import io.wexchain.dccchainservice.type.TnOrderStatus

/**
 *Created by liuyang on 2018/12/5.
 */
data class TnLoanOrder(
        val id: String,
        val chainOrderId: String,
        val address: String,
        val failCode: String,
        val failMessage: String,
        val status: TnOrderStatus,
        val repaymentTime: Long,
        val createdTime: String,
        val orderInfoMap: OrderInfoMap,
        val loanOrder: LoanOrder,
        val auditTime: String?,
        val remark: String,
        val approveAmount: String,
        val approveDuration: String,
        val approveDurationUnit: String,
        val canLoanTime: String?,
        val orderId: String,
        val principle: String,
        val interestFee: String,
        val serviceFee: String,
        val totalAmount: String,
        val alreadyPaidAmount: String,
        val loanTime: String,
        val loanTcanRepayTimeime: String,
        val canRepayTime: String,
        val actualRepayTime: String,
        val overdueDay: String,
        val overdueFee: String,
        val borrowAmount: String,
        val duration: String,
        val durationUnit: String,
        val interestRate: String,
        val receiveBankCode: String,
        val receiveBankName: String,
        val receiveBankCardTailNumber: String,
        val repayAmount: String,
        val repayTime: String
) {

    data class LoanOrder(
            val id: String,
            val chainOrderId: String,
            val address: String,
            val failCode: String,
            val failMessage: String,
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
