package io.wexchain.dccchainservice.domain
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.math.BigInteger


data class EcoDayIncome(
    @SerializedName("yesterdayAmount") val yesterdayAmount: BigDecimal?,
    @SerializedName("amount") val amount: BigInteger?
)