package io.wexchain.dccchainservice.domain
import com.google.gson.annotations.SerializedName
import java.math.BigInteger


data class EcoBonus(
    @SerializedName("id") val id: Long,
    @SerializedName("receiverAddress") val receiverAddress: String,
    @SerializedName("amount") val amount: BigInteger?,
    @SerializedName("memo") val memo: String?,
    @SerializedName("createdTime") val createdTime: Long
)