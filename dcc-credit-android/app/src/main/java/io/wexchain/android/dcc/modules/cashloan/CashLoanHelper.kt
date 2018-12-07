package io.wexchain.android.dcc.modules.cashloan

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.domain.TnLoanOrder
import io.wexchain.dccchainservice.type.TnOrderStatus

/**
 *Created by liuyang on 2018/12/6.
 */
object CashLoanHelper {

    @JvmStatic
    fun loanRejected(status: TnOrderStatus?): Boolean {
        if (status == null) {
            return false
        }
        if (status == TnOrderStatus.REJECTED) {
            return true
        } else {
            return false
        }
    }

    @JvmStatic
    fun loanStatus(status: TnOrderStatus?): Boolean {
        return when (status) {
            TnOrderStatus.NONE, TnOrderStatus.DELAYED, TnOrderStatus.DELIVERIED, TnOrderStatus.CREATED -> true
            else -> false
        }
    }

    @JvmStatic
    fun loanDelivering(status: TnOrderStatus?, type: Boolean): Boolean {
        return TnOrderStatus.AUDITING == status && type
    }

    @JvmStatic
    fun Context.loanStatusBackground(status: TnOrderStatus?): Drawable? {
        return when (status) {
            TnOrderStatus.DELAYED -> ContextCompat.getDrawable(this, R.drawable.bg_myloan_overdue)
            TnOrderStatus.REPAID -> ContextCompat.getDrawable(this, R.drawable.bg_myloan_repaid)
            TnOrderStatus.CANCELLED -> ContextCompat.getDrawable(this, R.drawable.bg_loan_status_other)
            TnOrderStatus.DELIVERIED -> ContextCompat.getDrawable(this, R.drawable.bg_myloan_success)
            TnOrderStatus.FAILURE -> ContextCompat.getDrawable(this, R.drawable.bg_myloan_failed)
            TnOrderStatus.DELIVERING -> ContextCompat.getDrawable(this, R.drawable.bg_myloan_delivering)
            TnOrderStatus.AUDITING -> ContextCompat.getDrawable(this, R.drawable.bg_myloan_auditing)
            else -> ContextCompat.getDrawable(this, R.drawable.bg_loan_status_other)
        }
    }

    @JvmStatic
    fun getOrderid(order: TnLoanOrder?): String {
        return order?.let {
            "订单号: ${it.orderId}"
        } ?: ""
    }

    @JvmStatic
    fun getStatusTxt(status: TnOrderStatus?): String {
        return when (status) {
            TnOrderStatus.DELAYED -> "已逾期"
            TnOrderStatus.REPAID -> "已结清"
            TnOrderStatus.CANCELLED -> "已关闭"
            TnOrderStatus.DELIVERIED -> "放款成功"
            TnOrderStatus.FAILURE -> "放款失败"
            TnOrderStatus.DELIVERING -> "放款中"
            else -> ""
        }
    }




}