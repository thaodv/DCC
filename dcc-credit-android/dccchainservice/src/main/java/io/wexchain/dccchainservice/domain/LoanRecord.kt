package io.wexchain.dccchainservice.domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger

data class LoanRecord(
    @SerializedName("orderId") val orderId: Long,
    @SerializedName("borrower") val borrower: String,
    @SerializedName("status") val status: LoanStatus,
    @SerializedName("fee") val fee: BigInteger,
    @SerializedName("receiveAddress") val receiveAddress: String,
    @SerializedName("loanProductId") val loanProductId: Long?,
    @SerializedName("currency") val currency: LoanCurrency?,
    @SerializedName("loanRate") val loanRate: BigDecimal?,
    @SerializedName("lender") val lender: Lender?,
    @SerializedName("repayAheadRate") val repayAheadRate: BigDecimal?,
    @SerializedName("applyId") val applyId: String?,
    @SerializedName("borrowAmount") val borrowAmount: BigDecimal?,
    @SerializedName("borrowDuration") val borrowDuration: Int?,
    @SerializedName("durationType") val durationType: LoanPeriodTimeUnit?,
    @SerializedName("applyDate") val applyDate: Long?,
    @SerializedName("gmtCreate") val gmtCreate: Long?,
    @SerializedName("expirationTime") val expirationTime: Long?
):Serializable {
}