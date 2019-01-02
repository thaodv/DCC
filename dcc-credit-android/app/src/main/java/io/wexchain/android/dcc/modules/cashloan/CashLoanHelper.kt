package io.wexchain.android.dcc.modules.cashloan

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
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
        return (TnOrderStatus.CREATED == status || TnOrderStatus.AUDITING == status) && type
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
            TnOrderStatus.CREATED, TnOrderStatus.AUDITING -> ContextCompat.getDrawable(this, R.drawable.bg_myloan_auditing)
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
            TnOrderStatus.NONE, TnOrderStatus.REPAID, TnOrderStatus.CREATED -> "申请额度"
            else -> ""
        }
    }

    @JvmStatic
    fun isshowLoanTips(status: TnOrderStatus?): Boolean {
        return when (status) {
            TnOrderStatus.NONE, TnOrderStatus.REPAID, TnOrderStatus.DELAYED, TnOrderStatus.CREATED -> true
            else -> false
        }
    }

    @JvmStatic
    fun loanMaxtxt(status: TnOrderStatus?): String {
        return when (status) {
            TnOrderStatus.AUDITED -> "借款额度"
            TnOrderStatus.DELIVERIED, TnOrderStatus.DELAYED -> "借款金额已到账"
            TnOrderStatus.NONE, TnOrderStatus.REPAID, TnOrderStatus.CREATED -> "最高可借"
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
            TnOrderStatus.NONE, TnOrderStatus.REPAID, TnOrderStatus.CREATED -> isTrue
            TnOrderStatus.DELIVERIED, TnOrderStatus.DELAYED, TnOrderStatus.AUDITED -> !isTrue
            else -> false
        }
    }

    @JvmStatic
    fun loanDetailTips(status: TnOrderStatus?): String {
        return when (status) {
            TnOrderStatus.DELAYED -> "您的订单已逾期，会产生额外的逾期费用，请及时还款"
            TnOrderStatus.REPAID -> "您的借款订单已结清"
            TnOrderStatus.CANCELLED -> "您的借款订单已经关闭"
            TnOrderStatus.DELIVERIED -> "您的借款已经打入尾号（4622）的银行卡，请查收"
            TnOrderStatus.DELIVERING -> "您的借款正在放款中，请稍后..."
            TnOrderStatus.FAILURE -> "放款失败，请检查您的银行卡并再次进行提现"
            else -> ""
        }
    }

    @JvmStatic
    @ColorInt
    fun Context.loanDetailColor(status: TnOrderStatus?): Int {
        return when (status) {
            TnOrderStatus.DELAYED -> ContextCompat.getColor(this, R.color.FFED190F)
            TnOrderStatus.REPAID, TnOrderStatus.DELIVERIED -> ContextCompat.getColor(this, R.color.FF000000)
            TnOrderStatus.DELIVERING, TnOrderStatus.CANCELLED -> ContextCompat.getColor(this, R.color.FFBAC0C5)
            TnOrderStatus.FAILURE -> ContextCompat.getColor(this, R.color.FFD90C0C)
            else -> ContextCompat.getColor(this, R.color.FFFC318C)
        }
    }

    @JvmStatic
    fun loanConfirmTxt(status: TnOrderStatus?): String {
        return when (status) {
            TnOrderStatus.DELAYED -> "还款"
            TnOrderStatus.REPAID -> "再次借款"
            TnOrderStatus.DELIVERIED -> "提前还款"
            TnOrderStatus.FAILURE -> "提现"
            else -> ""
        }
    }

    @JvmStatic
    fun loanShowItem1(status: TnOrderStatus?): Boolean {
        return when (status) {
            TnOrderStatus.CANCELLED -> true
            else -> false
        }
    }

    @JvmStatic
    fun loanShowItem2(status: TnOrderStatus?): Boolean {
        return when (status) {
            TnOrderStatus.DELIVERING -> true
            else -> false
        }
    }

    @JvmStatic
    fun loanShowItem3(status: TnOrderStatus?): Boolean {
        return when (status) {
            TnOrderStatus.FAILURE, TnOrderStatus.DELIVERIED, TnOrderStatus.REPAID, TnOrderStatus.DELAYED -> true
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