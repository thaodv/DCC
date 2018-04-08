package io.wexchain.android.dcc.vm

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.auth.R
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.api.domain.front.Quote
import io.wexchain.digitalwallet.util.toBigDecimalSafe
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

object ViewModelHelper {
    @JvmStatic
    fun Context.getAuthTypeIcon(certificationType: CertificationType?): Drawable? {
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