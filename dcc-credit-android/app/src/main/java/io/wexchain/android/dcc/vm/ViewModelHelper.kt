package io.wexchain.android.dcc.vm

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.*
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.api.domain.front.Quote
import io.wexchain.digitalwallet.util.toBigDecimalSafe
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

object ViewModelHelper {

    private val expiredFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    @JvmStatic
    fun expiredText(expired: Long?): String {
        if (expired == null || expired <= 0) {
            return ""
        }
        return expiredFormat.format(expired)
    }

    @JvmStatic
    fun Context.getCertTypeIcon(certificationType: CertificationType?): Drawable? {
        val drawableId = when (certificationType) {
            null -> 0
            CertificationType.ID -> R.drawable.shape_id
            CertificationType.PERSONAL -> R.drawable.shape_personal
            CertificationType.BANK -> R.drawable.shape_bank_card
            CertificationType.MOBILE -> R.drawable.shape_mobile
        }
        return ContextCompat.getDrawable(this, drawableId)
    }

    @JvmStatic
    fun Context.getCertStatusOpIcon(userCertStatus: UserCertStatus?): Drawable? {
        return when (userCertStatus) {
            UserCertStatus.INCOMPLETE -> ContextCompat.getDrawable(this, R.drawable.progress_indeterminate_gear)
            UserCertStatus.NONE, UserCertStatus.DONE -> ContextCompat.getDrawable(this, R.drawable.arrow_right)
            else -> null
        }
    }

    @JvmStatic
    fun Context.getCertStatusOpText(userCertStatus: UserCertStatus?): String {
        return when (userCertStatus) {
            UserCertStatus.NONE -> "未认证"
            UserCertStatus.INCOMPLETE -> "认证中"
            UserCertStatus.DONE -> "认证完成"
            else -> ""
        }
    }

    @JvmStatic
    @ColorInt
    fun Context.getCertStatusOpTextColor(userCertStatus: UserCertStatus?): Int {
        return when (userCertStatus) {
            UserCertStatus.NONE -> ContextCompat.getColor(this, R.color.text_dark)
            UserCertStatus.INCOMPLETE -> ContextCompat.getColor(this, R.color.text_blue_magenta)
            UserCertStatus.DONE -> ContextCompat.getColor(this, R.color.text_dark)
            else -> ContextCompat.getColor(this, R.color.text_dark)
        }
    }

    @JvmStatic
    fun Context.getMarketingActivityStatusText(status: MarketingActivity.Status?): String {
        return when (status) {
            MarketingActivity.Status.SHELVED -> "未开始"
            MarketingActivity.Status.STARTED -> "进行中"
            MarketingActivity.Status.ENDED -> "已结束"
            null -> ""
        }
    }

    @JvmStatic
    fun Context.getMarketingActivityStartTimeText(ma: MarketingActivity?): String? {

        return ma?.let {
            val date = MarketingActivity.dateFormat.parse(it.startTime)
            "开始时间:${getString(R.string.time_format_yyyymmdd, date)}"
        } ?: ""
    }

    @JvmStatic
    @ColorInt
    fun Context.getCertStatusBarColor(userCertStatus: UserCertStatus): Int {
        return when (userCertStatus) {
            UserCertStatus.DONE -> ContextCompat.getColor(this, R.color.text_blue_magenta)
            else -> return Color.TRANSPARENT
        }
    }

    @JvmStatic
    fun Context.getMarketingScenarioActionText(status: MarketingActivityScenario.Qualification?): String {
        return when (status) {
            MarketingActivityScenario.Qualification.REDEEMED -> "已领取"
            MarketingActivityScenario.Qualification.AVAILABLE -> "领取"
            null -> "待认证"
        }
    }

    @JvmStatic
    fun maskAddress(address: String?): String? {
        return if (address != null && address.length > 6) {
            return "***${address.substring(address.length - 6)}"
        } else null
    }

    @JvmStatic
    fun maskAddress2(address: String?): String? {
        return if (address != null && address.length >= 8) {
            val length = address.length
            return "0X ${address.substring(length - 8, length - 4).toUpperCase()} ${address.substring(length - 4)}"
        } else null
    }

    @JvmStatic
    fun maskCnId(id:String?): String? {
        id?:return null
        require(id.length == 15 || id.length == 18)
        return "${id.substring(0,6)}********${id.substring(id.length-4)}"
    }

    @JvmStatic
    fun Context.getIconFor(appid: String): Drawable? {
        return when (appid) {
        // 微财富 appId = 1 , appName = com.weicaifu.wcf
//            "1" -> ContextCompat.getDrawable(context, R.drawable.wcflogo)
            else -> ContextCompat.getDrawable(
                this,
                R.drawable.ic_launcher
            )
        }
    }

    @JvmStatic
    fun Context.smsTimeText(resend: Long?): String? {
        if (canResendSms(resend)) {
            return this.getString(R.string.send_sms_code)
        } else {
            return "$resend s"
        }
    }

    @JvmStatic
    fun canResendSms(resend: Long?): Boolean {
        return (resend == null || resend <= 0L)
    }

    @JvmStatic
    fun getDccStrPlus(holding: BigInteger?): String {
        return holding?.let {
            val holdingStr = Currencies.DCC.toDecimalAmount(it)
                .setScale(2, RoundingMode.DOWN).toPlainString()
            "+$holdingStr DCC"
        } ?: "--"
    }

    @JvmStatic
    fun getDccStr(holding: BigInteger?): String {
        return holding?.let {
            val holdingStr = Currencies.DCC.toDecimalAmount(it)
                .setScale(2, RoundingMode.DOWN).toPlainString()
            "$holdingStr DCC"
        } ?: "--"
    }

    @JvmStatic
    fun getBalanceStr(dc: DigitalCurrency, holding: BigInteger?): String {
        return holding?.let { dc.toDecimalAmount(it).currencyToDisplayStr() }
                ?: "--"
    }

    @JvmStatic
    fun getApproxValueStr(dc: DigitalCurrency, holding: BigInteger?, quote: Quote?): String {
        if (holding != null && quote?.price != null) {
            val price = quote.price!!.toBigDecimalSafe()
            val value = dc.toDecimalAmount(holding) * price
            if (value.signum() != 0) {
                return "~${quote.currencySymbol}${value.currencyToDisplayStr()}"
            }
        }
        return "--"
    }

    @JvmStatic
    fun formatCurrencyValue(value: String?): String {
        value ?: return ""
        return value.toBigDecimalSafe().currencyToDisplayStr()
    }

    @JvmStatic
    fun formatCurrencyValue(value: BigDecimal?): String {
        value ?: return ""
        return value.currencyToDisplayStr()
    }

    @JvmStatic
    fun text2LinesBeneficiaryAddress(beneficiaryAddress: BeneficiaryAddress?): String {
        return beneficiaryAddress?.run { "$shortName\n$address" } ?: ""
    }

    @JvmStatic
    fun Context.requisiteListStr(product: LoanProduct?, completed: List<String>?): CharSequence {
        return if (product != null) {
            product.requisiteCertList.joinTo(SpannableStringBuilder(), separator = "、") {
                val text = when (it) {
                    ChainGateway.BUSINESS_ID -> "身份证信息"
                    ChainGateway.BUSINESS_BANK_CARD -> "银行卡信息"
                    ChainGateway.BUSINESS_COMMUNICATION_LOG -> "运营商信息"
                    else -> it
                }
                if (completed?.contains(it) == true) {
                    text
                } else {
                    SpannableString(text).apply {
                        setSpan(
                            ForegroundColorSpan(ContextCompat.getColor(this@requisiteListStr, R.color.text_red)),
                            0,
                            length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        } else ""
    }


    @JvmStatic
    fun isCreating(status: LoanStatus?): Boolean {
        return when (status) {
            LoanStatus.INVALID -> false//todo
            LoanStatus.CREATED -> true
            LoanStatus.CANCELLED -> true
            LoanStatus.AUDITING -> false
            LoanStatus.REJECTED -> false
            LoanStatus.APPROVED -> false
            LoanStatus.FAILURE -> false
            LoanStatus.DELIVERED -> false
            LoanStatus.RECEIVIED -> false
            LoanStatus.REPAID -> false
            null -> false
        }
    }

    @JvmStatic
    fun Context.loanStatusText(status: LoanStatus?): CharSequence? {
        return when (status) {
            LoanStatus.INVALID -> "已失效"
            LoanStatus.CREATED -> "订单创建中"
            LoanStatus.CANCELLED -> "已取消"
            LoanStatus.AUDITING -> "审核中"
            LoanStatus.REJECTED -> "审核失败"
            LoanStatus.APPROVED -> "审核成功"
            LoanStatus.FAILURE -> "放币失败"
            LoanStatus.DELIVERED -> "已放币"
            LoanStatus.RECEIVIED -> "已收币"
            LoanStatus.REPAID -> "已还币"
            null -> getString(R.string.empty_slash)
        }
    }

    @JvmStatic
    fun Context.loanStatusBackground(status: LoanStatus?): Drawable? {
        return when (status) {
            LoanStatus.DELIVERED -> ContextCompat.getDrawable(this, R.drawable.bg_loan_status_delivered)
            LoanStatus.REPAID -> ContextCompat.getDrawable(this, R.drawable.bg_loan_status_repaid)
            LoanStatus.INVALID,
            LoanStatus.CANCELLED,
            LoanStatus.CREATED -> ContextCompat.getDrawable(this, R.drawable.bg_loan_status_created)
            LoanStatus.APPROVED -> ContextCompat.getDrawable(this, R.drawable.bg_loan_status_approved)
            LoanStatus.AUDITING,
            LoanStatus.REJECTED,
            LoanStatus.FAILURE,
            LoanStatus.RECEIVIED,
            null -> ContextCompat.getDrawable(this, R.drawable.bg_loan_status_other)
        }
    }

    @JvmStatic
    fun Context.loanStatusNoticeText(status: LoanStatus?): CharSequence? {
        return when (status) {
            LoanStatus.INVALID -> null//todo
            LoanStatus.CREATED -> null//todo
            LoanStatus.CANCELLED -> null//todo
            LoanStatus.AUDITING -> "您的借币申请正在审核中"
            LoanStatus.REJECTED -> "您的借币申请审核失败\n建议过段时间(1个月)再尝试"
            LoanStatus.APPROVED -> "更新你的申请已经审核通过\n我们将尽快打币"
            LoanStatus.FAILURE -> "很遗憾放币失败\n建议过短时间(一周后)再尝试"
            LoanStatus.DELIVERED -> "已放币"
            LoanStatus.RECEIVIED -> "已收币"
            LoanStatus.REPAID -> "您的订单已处理完毕"
            null -> null
        }
    }

    @JvmStatic
    fun Context.loanStatusNoticeIcon(status: LoanStatus?): Drawable? {
        return when (status) {
            LoanStatus.INVALID -> null//todo
            LoanStatus.CREATED -> null//todo
            LoanStatus.CANCELLED -> null//todo
            LoanStatus.AUDITING -> ContextCompat.getDrawable(this, R.drawable.ic_loan_auditing)
            LoanStatus.REJECTED -> ContextCompat.getDrawable(this, R.drawable.ic_loan_rejected)
            LoanStatus.APPROVED -> ContextCompat.getDrawable(this, R.drawable.ic_loan_approved)
            LoanStatus.FAILURE -> ContextCompat.getDrawable(this, R.drawable.ic_loan_failure)
            LoanStatus.RECEIVIED,//treat same as DELIVERED
            LoanStatus.DELIVERED -> ContextCompat.getDrawable(this, R.drawable.ic_loan_delivered)
            LoanStatus.REPAID -> ContextCompat.getDrawable(this, R.drawable.ic_loan_repaid)
            null -> null
        }
    }

    @JvmStatic
    fun Context.loanStatusAction(record: LoanRecord?): CharSequence? {
        record ?: return null
        return when (record.status) {
            LoanStatus.INVALID -> null//todo
            LoanStatus.CREATED -> null//todo
            LoanStatus.CANCELLED -> null//todo
            LoanStatus.AUDITING -> null
            LoanStatus.APPROVED -> null
            LoanStatus.REJECTED,
            LoanStatus.FAILURE,
            LoanStatus.REPAID -> "重新申请"
            LoanStatus.DELIVERED,
            LoanStatus.RECEIVIED -> {
                if (!record.earlyRepayAvailable) {// not early now
                    "还币"
                } else {
                    if (record.allowRepayPermit) {
                        "提前还币"
                    } else null
                }
            }
        }
    }

    @JvmStatic
    fun Context.loanPeriodText(record: LoanRecord?): CharSequence? {
        record ?: return null
        return when (record.status) {
            LoanStatus.INVALID,
            LoanStatus.CREATED,
            LoanStatus.CANCELLED -> ""//todo
            LoanStatus.AUDITING,
            LoanStatus.REJECTED,
            LoanStatus.APPROVED,
            LoanStatus.FAILURE -> "${record.borrowDuration}${record.durationUnit.str()}"
            LoanStatus.DELIVERED,
            LoanStatus.RECEIVIED,
            LoanStatus.REPAID -> if (record.repayDate != null && record.deliverDate != null) {
                getString(R.string.period_format_yyyymmdd_dot, record.deliverDate, record.repayDate)
            } else {
                "${record.borrowDuration}${record.durationUnit.str()}"
            }
        }
    }


    @JvmStatic
    fun Context.loanAmountText(record: LoanRecord?): CharSequence? {
        record ?: return null
        return "${record.amount.toPlainString()}${record.currency.symbol}"
    }

    @JvmStatic
    fun concatWithoutNull(vararg texts: String?): String {
        return texts.filterNotNull().joinToString(separator = "") { it }
    }

    @JvmStatic
    fun Context.loanReportBg(loanReport: LoanReport?): Drawable? {
        return if(loanReport == null || loanReport.isFromOtherAddress()){
             ContextCompat.getDrawable(this, R.drawable.bg_loan_report_status_settlement_other)
        }else{
            when (loanReport?.status) {
                LoanStatus.REPAID -> ContextCompat.getDrawable(this, R.drawable.bg_loan_report_status_settlement_complete)
                else -> ContextCompat.getDrawable(this, R.drawable.bg_loan_report_status_settlement_incomplete)
            }
        }
    }

    @JvmStatic
    fun Context.billStatusStr(bill: LoanReport.Bill?): CharSequence? {
        return when (bill?.status) {
            LoanReport.Bill.BillStatus.VERIFIED,
            LoanReport.Bill.BillStatus.PAY_OFF -> "已结清"
            LoanReport.Bill.BillStatus.WAITING_VERIFY,
            LoanReport.Bill.BillStatus.CREATED,
            LoanReport.Bill.BillStatus.CANCELED -> "未结清"
            else -> null
        }
    }
}

fun BigDecimal.currencyToDisplayStr(): String {
    return this.setScale(4, RoundingMode.DOWN).toPlainString()
}