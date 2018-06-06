package io.wexchain.dccchainservice.domain
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class EcoBonus(
    @SerializedName("id") val id: Long,
    @SerializedName("receiverAddress") val receiverAddress: String,
    @SerializedName("amount") val amount: BigDecimal?,
    @SerializedName("memo") val memo: String?,
    @SerializedName("createdTime") val createdTime: Long
)