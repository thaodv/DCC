package io.wexchain.android.dcc.vm

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.auth.R
import io.wexchain.dccchainservice.domain.MarketingActivity
import io.wexchain.dccchainservice.domain.MarketingActivityScenario
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
            return "0X ${address.substring(length - 8, length - 4).toUpperCase()} ${address.substring(
                    length - 4
            )}"
        } else null
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
    fun getDccStr(holding: BigInteger?): String {
        return holding?.let {
            val holdingStr = Currencies.DCC.toDecimalAmount(it)
                    .setScale(2, RoundingMode.DOWN).toPlainString()
            "+$holdingStr DCC"
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
}

fun BigDecimal.currencyToDisplayStr(): String {
    return this.setScale(4, RoundingMode.DOWN).toPlainString()
}