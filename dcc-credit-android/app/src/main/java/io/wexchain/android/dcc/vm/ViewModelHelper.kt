package io.wexchain.android.dcc.vm

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.auth.R
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
    fun expiredText(expired:Long?): String {
        if(expired==null || expired<=0){
            return ""
        }
        return expiredFormat.format(expired)
    }

    @JvmStatic
    fun Context.getCertTypeIcon(certificationType: CertificationType?): Drawable? {
        val drawableId = when(certificationType){
            null -> 0
            CertificationType.ID -> R.drawable.shape_id
            CertificationType.PERSONAL -> R.drawable.shape_personal
            CertificationType.BANK -> R.drawable.shape_bank_card
            CertificationType.MOBILE -> R.drawable.shape_mobile
        }
        return ContextCompat.getDrawable(this,drawableId)
    }

    @JvmStatic
    fun Context.getCertStatusOpIcon(userCertStatus: UserCertStatus?):Drawable?{
        return when(userCertStatus){
            UserCertStatus.INCOMPLETE -> ContextCompat.getDrawable(this,R.drawable.progress_indeterminate_gear)
            UserCertStatus.NONE, UserCertStatus.DONE -> ContextCompat.getDrawable(this,R.drawable.arrow_right)
            else -> null
        }
    }
    @JvmStatic
    fun Context.getCertStatusOpText(userCertStatus: UserCertStatus?):String{
        return when(userCertStatus){
            UserCertStatus.NONE -> "未认证"
            UserCertStatus.INCOMPLETE -> "认证中"
            UserCertStatus.DONE -> "认证完成"
            else -> ""
        }
    }

    @JvmStatic
    @ColorInt
    fun Context.getCertStatusOpTextColor(userCertStatus: UserCertStatus?):Int{
        return when(userCertStatus){
            UserCertStatus.NONE -> ContextCompat.getColor(this,R.color.text_dark_alpha)
            UserCertStatus.INCOMPLETE -> ContextCompat.getColor(this,R.color.text_red)
            UserCertStatus.DONE -> ContextCompat.getColor(this,R.color.text_blue_magenta)
            else -> ContextCompat.getColor(this,R.color.text_dark_alpha)
        }
    }

    @JvmStatic
    @ColorInt
    fun Context.getCertStatusBarColor(userCertStatus: UserCertStatus):Int{
        return when(userCertStatus){
            UserCertStatus.DONE -> ContextCompat.getColor(this,R.color.text_blue_magenta)
            else -> return Color.TRANSPARENT
        }
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