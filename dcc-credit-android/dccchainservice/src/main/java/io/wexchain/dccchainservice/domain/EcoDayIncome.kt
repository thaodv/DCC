package io.wexchain.dccchainservice.domain
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class EcoDayIncome(
    @SerializedName("yesterdayAmount") val yesterdayAmount: BigDecimal,
    @SerializedName("amount") val amount: BigDecimal
)