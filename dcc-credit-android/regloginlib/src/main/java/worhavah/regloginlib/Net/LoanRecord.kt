package worhavah.regloginlib.Net

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger

//data class LoanRecord(
//    @SerializedName("orderId") val orderId: Long,
//    @SerializedName("borrower") val borrower: String,
//    @SerializedName("status") val status: LoanStatus,
//    @SerializedName("fee") val fee: BigDecimal,
//    @SerializedName("receiveAddress") val receiveAddress: String,
//    @SerializedName("loanProductId") val loanProductId: Long?,
//    @SerializedName("currency") val currency: LoanCurrency?,
//    @SerializedName("loanRate") val loanRate: BigDecimal?,
//    @SerializedName("lender") val lender: Lender?,
//    @SerializedName("repayAheadRate") val repayAheadRate: BigDecimal?,
//    @SerializedName("applyId") val applyId: String?,
//    @SerializedName("borrowAmount") val borrowAmount: BigDecimal?,
//    @SerializedName("borrowDuration") val borrowDuration: Int?,
//    @SerializedName("durationType") val durationType: LoanPeriodTimeUnit?,
//    @SerializedName("applyDate") val applyDate: Long?,
//    @SerializedName("gmtCreate") val gmtCreate: Long?,
//    @SerializedName("expirationTime") val expirationTime: Long?
//):Serializable {
//}

data class LoanRecord(
    @SerializedName("orderId") val orderId: Long,
    @SerializedName("status") val status: LoanStatus,
    @SerializedName("applyDate") val applyDate: Long,
    @SerializedName("currency") val currency: LoanCurrency,
    @SerializedName("lender") val lender: Lender,
    @SerializedName("amount") val amount: BigDecimal,
    @SerializedName("durationUnit") val durationUnit: LoanPeriodTimeUnit,
    @SerializedName("borrowDuration") val borrowDuration: Int,
    @SerializedName("fee") val fee: BigDecimal,
    @SerializedName("receiverAddress") val receiverAddress: String,
    @SerializedName("deliverDate") val deliverDate: Long?,
    @SerializedName("repayDate") val repayDate: Long?,
    @SerializedName("expectRepayDate") val expectRepayDate: Long?,
    @SerializedName("loanInterest") val loanInterest: BigDecimal?,
    @SerializedName("expectLoanInterest") val expectLoanInterest: BigDecimal?,
    @SerializedName("earlyRepayAvailable") val earlyRepayAvailable: Boolean,
    @SerializedName("allowRepayPermit") val allowRepayPermit: Boolean
){
    fun getOrderIdStr() = "$orderId"

    fun showContract():Boolean {
        return when(status){
            LoanStatus.DELIVERED ,
            LoanStatus.RECEIVIED ,
            LoanStatus.REPAID -> true
            else -> false
        }
    }
}