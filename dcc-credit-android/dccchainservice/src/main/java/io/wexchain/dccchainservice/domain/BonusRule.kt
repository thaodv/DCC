package io.wexchain.dccchainservice.domain
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class BonusRule(
    @SerializedName("groupCode") val groupCode: String,
    @SerializedName("bonusName") val bonusName: String,
    @SerializedName("bonusCode") val bonusCode: String,
    @SerializedName("bonusAmount") val bonusAmount: BigDecimal
){
    companion object {
        const val GROUP_BASE = "BASE"
        const val GROUP_BORROW = "BORROW"
        const val GROUP_REPAY = "REPAY"
    }
}