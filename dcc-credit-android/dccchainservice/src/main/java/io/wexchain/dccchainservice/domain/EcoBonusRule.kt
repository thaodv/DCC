package io.wexchain.dccchainservice.domain
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class EcoBonusRule(
    @SerializedName("groupCode") val groupCode: String,
    @SerializedName("bonusName") val bonusName: String,
    @SerializedName("bonusCode") val bonusCode: String,
    @SerializedName("bonusAmount") val bonusAmount: BigDecimal
)