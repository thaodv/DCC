package io.wexchain.dccchainservice.domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal


data class LoanRepaymentBill(
    @SerializedName("chainOrderId") val chainOrderId: Long,
    @SerializedName("repaymentInterest") val repaymentInterest: BigDecimal,
    @SerializedName("repaymentPrincipal") val repaymentPrincipal: BigDecimal,
    @SerializedName("overdueFine") val overdueFine: BigDecimal?,
    @SerializedName("penaltyAmount") val penaltyAmount: BigDecimal?,
    @SerializedName("amount") val amount: BigDecimal,
    @SerializedName("assetCode") val assetCode: String,
    @SerializedName("repaymentAddress") val repaymentAddress: String
):Serializable {
    fun isOverdue(): Boolean {
        return overdueFine != null && overdueFine != BigDecimal.ZERO
    }

    fun isPenalty():Boolean{
        return penaltyAmount != null && penaltyAmount != BigDecimal.ZERO
    }
}