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
import io.wexchain.android.common.checkLanguage
import io.wexchain.android.common.versionInfo
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.tools.getString
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.*
import io.wexchain.dccchainservice.util.DateUtil
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.api.domain.front.CoinDetail
import io.wexchain.digitalwallet.api.domain.front.Quote
import io.wexchain.digitalwallet.util.computeTransCountKeep2Number
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
    fun expiredToLong(expired: String): Long {
        val list = expired.split('-')
        return if (list.size == 2) {
            val charArray = list[list.size - 1].toCharArray()
            var tmp = ""
            for ((i, char) in charArray.withIndex()) {
                tmp += if (i == 3 || i == 5) {
                    "$char-"
                } else {
                    char
                }
            }
            expiredFormat.parse(tmp).time
        } else {
            expiredFormat.parse(expired).time
        }

    }


    @JvmStatic
    fun Context.getCertTypeIcon(certificationType: CertificationType?): Drawable? {
        val drawableId = when (certificationType) {
            null -> 0
            CertificationType.ID -> R.drawable.shape_newid
            CertificationType.PERSONAL -> R.drawable.shape_personal
            CertificationType.BANK -> R.drawable.shape_newbankcard
            CertificationType.MOBILE -> R.drawable.shape_newmobile
            CertificationType.TONGNIU -> R.drawable.shape_newmobile
            CertificationType.LOANREPORT -> R.drawable.shape_newloanreport
        }
        return ContextCompat.getDrawable(this, drawableId)
    }

    @JvmStatic
    fun Context.getCertStatusOpText(userCertStatus: UserCertStatus?): String {
        return when (userCertStatus) {
            UserCertStatus.NONE -> getString(R.string.unverify_nowied)
            UserCertStatus.TIMEOUT -> "已过期"
            UserCertStatus.INCOMPLETE -> getString(R.string.verifying)
            UserCertStatus.DONE -> getString(R.string.verified)
            UserCertStatus.LOANREPORT -> "查看报告"
            else -> ""
        }
    }

    @JvmStatic
    fun Context.getCertStatusShape(userCertStatus: String?): Drawable? {
        val drawableId = when (userCertStatus) {
            "已认证" -> R.drawable.shape_certstatu_green
            "认证完成" -> R.drawable.shape_certstatu_green
            "已过期" -> R.drawable.shape_certstatu_red
            "未认证" -> R.drawable.shape_certstatu_red
            "查看报告" -> R.drawable.shape_certstatu_purple
            "认证中" -> R.drawable.shape_certstatu_blue
            else -> R.drawable.shape_certstatu_red

        }
        return ContextCompat.getDrawable(this, drawableId)
    }

    @JvmStatic
    @ColorInt
    fun Context.getCertStatusOpTextColor(userCertStatus: UserCertStatus?): Int {
        return when (userCertStatus) {
            UserCertStatus.NONE -> ContextCompat.getColor(this, R.color.text_dark)
            UserCertStatus.TIMEOUT -> ContextCompat.getColor(this, R.color.FFED190F)
            UserCertStatus.INCOMPLETE -> ContextCompat.getColor(this, R.color.text_blue_magenta)
            UserCertStatus.DONE -> ContextCompat.getColor(this, R.color.text_dark)
            else -> ContextCompat.getColor(this, R.color.text_dark)
        }
    }

    @JvmStatic
    fun Context.getMarketingActivityStatusText(status: MarketingActivity.Status?): String {
        return when (status) {
            MarketingActivity.Status.SHELVED -> "未开始"
            MarketingActivity.Status.STARTED -> getString(R.string.ongoing)
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
            MarketingActivityScenario.Qualification.REDEEMED -> getString(R.string.collected)
            MarketingActivityScenario.Qualification.AVAILABLE -> getString(R.string.unclaimedcollect_now)
            null -> getString(R.string.to_be_verified)
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
            return "0x ${address.substring(length - 8, length - 4).toUpperCase()} ${address.substring(length - 4)}"
        } else null
    }

    @JvmStatic
    fun maskCnId(id: String?): String? {
        id ?: return null
        require(id.length == 15 || id.length == 18)
        return "${id.substring(0, 6)}********${id.substring(id.length - 4)}"
    }

    @JvmStatic
    fun Context.getIconFor(appid: String): Drawable? {
        return ContextCompat.getDrawable(
                this,
                R.drawable.ic_launcher
        )
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
    fun Context.smsTimeText2(resend: Long?): String? {
        if (canResendSms(resend)) {
            return "发送"
        } else {
            return "$resend s"
        }
    }

    @JvmStatic
    fun canResendSms(resend: Long?): Boolean {
        return (resend == null || resend <= 1L)
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
    fun getDccStr(holding: BigInteger?): String? {
        return holding?.let {
            val holdingStr = Currencies.DCC.toDecimalAmount(it)
                    .currencyToDisplayStr()
            "$holdingStr DCC"
        } ?: ""
    }

    @JvmStatic
    fun getBalanceStr(dc: DigitalCurrency?, holding: BigInteger?): String {
        return holding?.let { dc!!.toDecimalAmount(it).currencyToDisplayStr() }
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
    fun getApproxValueStr(dc: DigitalCurrency, holding: BigInteger?, quote: CoinDetail?): String {
        if (holding != null && quote?.price != null) {
            val price = quote.price!!.toBigDecimalSafe()
            val value = dc.toDecimalAmount(holding) * price
            if (value.signum() != 0) {
                return "~¥${value.currencyToDisplayStr()}"
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
                    ChainGateway.BUSINESS_ID -> getString(R.string.id)
                    ChainGateway.BUSINESS_BANK_CARD -> getString(R.string.bank_accoun)
                    ChainGateway.BUSINESS_COMMUNICATION_LOG -> getString(R.string.carrier_information)
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
            LoanStatus.CREATED -> getString(R.string.creating_order)
            LoanStatus.CANCELLED -> "已取消"
            LoanStatus.AUDITING -> getString(R.string.pending_reviewing)
            LoanStatus.REJECTED -> getString(R.string.review_failed)
            LoanStatus.APPROVED -> getString(R.string.verify_success2)
            LoanStatus.FAILURE -> getString(R.string.loan_issuance_failed)
            LoanStatus.DELIVERED -> getString(R.string.loan_issued)
            LoanStatus.RECEIVIED -> "已收币"
            LoanStatus.REPAID -> getString(R.string.repaid)
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
            LoanStatus.AUDITING -> getString(R.string.your_loan_application_is_under_review)
            LoanStatus.REJECTED -> getString(R.string.loan_issuance_failed2)
            LoanStatus.APPROVED -> getString(R.string.your_application_has_been_approved)
            LoanStatus.FAILURE -> getString(R.string.please_try_again_after_a_while)
            LoanStatus.DELIVERED -> getString(R.string.loan_issued)
            LoanStatus.RECEIVIED -> getString(R.string.coin_received)
            LoanStatus.REPAID -> getString(R.string.your_order_has_been_processed)
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
            LoanStatus.REPAID -> getString(R.string.try_again)
            LoanStatus.DELIVERED,
            LoanStatus.RECEIVIED -> {
                if (!record.earlyRepayAvailable) {// not early now
                    getString(R.string.repayment)
                } else {
                    if (record.allowRepayPermit) {
                        getString(R.string.repay_in_advance_prepayment2)
                    } else null
                }
            }
        }
    }

    @JvmStatic
    fun Context.loanPeriodText(record: LoanRecord?): CharSequence? {
        record ?: return ""
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
                if (null == record.durationUnit) {
                    "${record.borrowDuration}天"
                } else {
                    "${record.borrowDuration}${record.durationUnit.str()}"
                }
            }
        }
    }


    @JvmStatic
    fun Context.loanAmountText(record: LoanRecord?): CharSequence? {
        record ?: return ""
        return "${record.amount.toPlainString()}${record.currency.symbol}"
    }

    @JvmStatic
    fun concatWithoutNull(vararg texts: String?): String {
        return texts.filterNotNull().joinToString(separator = "") { it }
    }

    @JvmStatic
    fun Context.loanReportBg(loanReport: LoanReport?): Drawable? {
        return if (loanReport == null || loanReport.isFromOtherAddress()) {
            ContextCompat.getDrawable(this, R.drawable.bg_loan_report_status_settlement_other)
        } else {
            if (!loanReport.isMort()) {
                when (loanReport?.status) {
                    LoanStatus.REPAID -> ContextCompat.getDrawable(
                            this,
                            R.drawable.bg_loan_report_status_settlement_complete
                    )
                    else -> ContextCompat.getDrawable(this, R.drawable.bg_loan_report_status_settlement_incomplete)
                }
            } else {
                when (loanReport?.mortgageStatus) {
                    MortgageStatus.REPAID -> ContextCompat.getDrawable(
                            this,
                            R.drawable.bg_loan_report_status_settlement_complete
                    )
                    else -> ContextCompat.getDrawable(this, R.drawable.bg_loan_report_status_settlement_incomplete)
                }
            }

        }
    }

    @JvmStatic
    fun Context.billStatusStr(bill: LoanReport.Bill?): CharSequence? {
        return when (bill?.status) {
            LoanReport.Bill.BillStatus.VERIFIED,
            LoanReport.Bill.BillStatus.PAY_OFF -> getString(R.string.settled)
            LoanReport.Bill.BillStatus.WAITING_VERIFY,
            LoanReport.Bill.BillStatus.CREATED,
            LoanReport.Bill.BillStatus.CANCELED -> getString(R.string.unsettled)
            else -> null
        }
    }

    @JvmStatic
    fun bonusAmountStr(redeemToken: RedeemToken?): String {
        redeemToken ?: return ""
        val amount = redeemToken.amount
        amount ?: return ""
        return Currencies.DCC.toDecimalAmount(amount).currencyToDisplayStr() + Currencies.DCC.symbol
    }

    @JvmStatic
    fun ecoBonusRuleAmountStr(ecoBonusRule: BonusRule?): CharSequence? {
        ecoBonusRule ?: return null
        return "+${ecoBonusRule.bonusAmount.toPlainString()}生态值"
    }

    @JvmStatic
    fun ecoBonusRewardAmountStr(ecoBonus: EcoBonus?): CharSequence? {
        val amount = ecoBonus?.amount
        amount ?: return null
        return "${Currencies.DCC.toDecimalAmount(amount).currencyToDisplayStr()}DCC"
    }

    @JvmStatic
    fun ecoBonusRuleGroupTitle(group: String?): CharSequence? {
        return when (group) {
            BonusRule.GROUP_BASE -> getString(R.string.basic_rewards)
            BonusRule.GROUP_BORROW -> getString(R.string.loan_application_rewards)
            BonusRule.GROUP_REPAY -> getString(R.string.repayment_rewards)
            else -> null
        }
    }

    @JvmStatic
    fun ecoBonusRuleGroupSlogan(group: String?): CharSequence? {
        return if (checkLanguage()) {
            null
        } else {
            when (group) {
                BonusRule.GROUP_BASE -> "认证就有奖"
                BonusRule.GROUP_BORROW -> "次数无上限，奖励无上限"
                BonusRule.GROUP_REPAY -> "按时还款，给信用加分"
                else -> null
            }
        }

    }

    @JvmStatic
    fun mineRewardAmountStr(mineCandy: MineCandy?): CharSequence? {
        mineCandy ?: return null
        return "+${Currencies.DCC.toDecimalAmount(mineCandy.amount).currencyToDisplayStr()}${Currencies.DCC.symbol}"
    }

    @JvmStatic
    fun transTime2Str(time: Long): String {
        return DateUtil.getStringTime(time * 1000, "HH:mm:ss")
    }

    @JvmStatic
    fun isPublic2Private(fromAssetCode: String, toAssetCode: String): Boolean {
        return fromAssetCode == "DCC" && toAssetCode == "DCC_JUZIX"
    }

    @JvmStatic
    fun accrossStatus(status: AccrossTransRecord.Status?): String {
        return when (status) {
            AccrossTransRecord.Status.ACCEPTED -> "转移中"
            AccrossTransRecord.Status.DELIVERED -> "已完成"
            else -> ""
        }
    }

    @JvmStatic
    fun accrossStatus(status: AccrossTransDetail.Status): String {
        return when (status) {
            AccrossTransRecord.Status.ACCEPTED -> "转移中"
            AccrossTransRecord.Status.DELIVERED -> "已完成"
            else -> ""
        }
    }

    @JvmStatic
    fun getTransCount(str: String): String {
        return computeTransCountKeep2Number(str.toBigDecimal()).toPlainString()
    }

    @JvmStatic
    fun getPoundge(str: String): String {
        return str + "DCC"
    }

    @JvmStatic
    fun getToAccountNum(str: BigDecimal, poundge: String): String {
        return (str.subtract(poundge.toBigDecimal())).toPlainString() + "DCC"
    }

    @JvmStatic
    fun getProtectStatus(protect: Protect): Boolean {
        return protect.type.get() != null
    }

    @JvmStatic
    fun Context.getVersion(): String {
        return getString(R.string.current_version) + versionInfo.versionName
    }

    @JvmStatic
    fun showBsxRate(rate: String): String {
        return "+$rate%"
    }

    @JvmStatic
    fun showBsxMinCount(min: String, type: String): String {
        return min + type
    }

    @JvmStatic
    fun showBsPeriod(period: String): String {
        return period + "天"
    }


    @JvmStatic
    fun showRedPacketGetable(count: String): String {
        return if ("0" == count) {
            "已解锁"
        } else {
            "未解锁"
        }
    }

    @JvmStatic
    fun showRedPacketGetTime(count: Long): String {
        return DateUtil.getStringTime(count, "MM-dd HH:mm:ss")
    }

    @JvmStatic
    fun showRedPacketGetMoney(count: String): String {
        return "￥" + count + "红包"
    }

    @JvmStatic
    fun showRedPacketInviteTime(count: Long): String {
        return DateUtil.getStringTime(count, "yyyy-MM-dd HH:mm:ss")
    }

    @JvmStatic
    fun Context.showRedPacketGetableBg(count: String): Drawable? {
        return when (count) {
            "0" -> ContextCompat.getDrawable(this, R.drawable.bg_redpacket_get_locked)
            else -> {
                ContextCompat.getDrawable(this, R.drawable.bg_redpacket_get_unlock)
            }
        }
    }

    @JvmStatic
    fun Context.showRedPacketBgStatus(count: String): Drawable? {
        return when (count) {
            "0" -> ContextCompat.getDrawable(this, R.drawable.bg_redpacket_level_rule_over)
            else -> {
                ContextCompat.getDrawable(this, R.drawable.bg_redpacket_level_rule)
            }
        }
    }

    @JvmStatic
    fun showRedPacketBgStatus(count: String): Boolean {
        return "0" == count
    }

    @JvmStatic
    fun Context.showBsxTopBgStatus(status: String): Drawable? {
        return when (status) {
            "3" -> ContextCompat.getDrawable(this, R.drawable.bg_bsx_market_item_top2)
            "4" -> ContextCompat.getDrawable(this, R.drawable.bg_bsx_market_item_top2)
            else -> {
                ContextCompat.getDrawable(this, R.drawable.bg_bsx_market_item_top)
            }
        }
    }

    @JvmStatic
    fun Context.showBsxBodyBgStatus(status: String): Drawable? {
        return when (status) {
            "3" -> ContextCompat.getDrawable(this, R.drawable.bg_bsx_market_item_body2)
            "4" -> ContextCompat.getDrawable(this, R.drawable.bg_bsx_market_item_body2)
            else -> {
                ContextCompat.getDrawable(this, R.drawable.bg_bsx_market_item_body)
            }
        }
    }

    @JvmStatic
    fun Context.showBsxStatus(status: String): Drawable? {
        return when (status) {
            "1" -> ContextCompat.getDrawable(this, R.drawable.img_bsx_status_1)
            "2" -> ContextCompat.getDrawable(this, R.drawable.img_bsx_status_2)
            "3" -> ContextCompat.getDrawable(this, R.drawable.img_bsx_status_3)
            else -> {
                ContextCompat.getDrawable(this, R.drawable.img_bsx_status_4)
            }
        }
    }


}

fun BigDecimal.currencyToDisplayStr(): String {
    return this.setScale(4, RoundingMode.DOWN).toPlainString()
}



