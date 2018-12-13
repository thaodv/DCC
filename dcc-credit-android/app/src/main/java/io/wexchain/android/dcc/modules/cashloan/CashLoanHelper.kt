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
    fun loanAuditing(status: TnOrderStatus?): Boolean {
        if (status == null) {
            return false
        }
        return status == TnOrderStatus.AUDITING
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

    @JvmStatic
    fun getRequestLoanTxt(status: TnOrderStatus?): String {
        return when (status) {
            TnOrderStatus.AUDITED -> "提现"
            TnOrderStatus.DELIVERIED -> "提前还款"
            TnOrderStatus.DELAYED -> "去还款"
            TnOrderStatus.NONE, TnOrderStatus.REPAID -> "申请额度"
            else -> ""
        }
    }

    @JvmStatic
    fun isshowLoanTips(status: TnOrderStatus?): Boolean {
        return when (status) {
            TnOrderStatus.NONE, TnOrderStatus.REPAID, TnOrderStatus.DELAYED -> true
            else -> false
        }
    }

    @JvmStatic
    fun loanMaxtxt(status: TnOrderStatus?): String {
        return when (status) {
            TnOrderStatus.AUDITED -> "借款额度"
            TnOrderStatus.DELIVERIED, TnOrderStatus.DELAYED -> "借款金额已到账"
            TnOrderStatus.NONE, TnOrderStatus.REPAID -> "最高可借"
            else -> ""
        }
    }

    @JvmStatic
    fun showAudited(status: TnOrderStatus?): Boolean {
        return when (status) {
            TnOrderStatus.AUDITED -> true
            else -> false
        }
    }

    @JvmStatic
    fun showRepayTime(status: TnOrderStatus?): Boolean {
        return when (status) {
            TnOrderStatus.DELIVERIED, TnOrderStatus.DELAYED -> true
            else -> false
        }
    }

    @JvmStatic
    fun showRepayTip(status: TnOrderStatus?): Boolean {
        return when (status) {
            TnOrderStatus.DELAYED -> true
            else -> false
        }
    }

    @JvmStatic
    fun showLoanLog(status: TnOrderStatus?, isTrue: Boolean): Boolean {
        return when (status) {
            TnOrderStatus.NONE, TnOrderStatus.REPAID -> isTrue
            TnOrderStatus.DELIVERIED, TnOrderStatus.DELAYED, TnOrderStatus.AUDITED -> !isTrue
            else -> false
        }
    }


    /*TnOrderStatus.DELAYED->{

    }
    TnOrderStatus.REPAID->{}
    TnOrderStatus.CANCELLED->{}
    TnOrderStatus.DELIVERIED->{}
    TnOrderStatus.FAILURE->{}
    TnOrderStatus.DELIVERING->{}
     */


}