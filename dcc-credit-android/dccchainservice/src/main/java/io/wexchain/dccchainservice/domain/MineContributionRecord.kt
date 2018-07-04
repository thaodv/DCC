package io.wexchain.dccchainservice.domain
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class MineContributionRecord(
    @SerializedName("address") val address: String,
    @SerializedName("score") val score: BigDecimal,
    @SerializedName("bonusName") val bonusName: String?,
    @SerializedName("createdTime") val createdTime: Long
)