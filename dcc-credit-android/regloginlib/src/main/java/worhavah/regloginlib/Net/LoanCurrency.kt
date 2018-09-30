package worhavah.regloginlib.Net

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger

data class LoanCurrency(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("decimal")
    val decimal: Int
): Serializable {
    fun convertToDecimal(value: BigInteger): BigDecimal {
        return value.toBigDecimal().scaleByPowerOfTen(-decimal)
    }
}