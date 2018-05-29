package io.wexchain.dccchainservice.domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal


data class LoanReport(
    @SerializedName("lenderName") val lenderName: String,
    @SerializedName("chainOrderId") val chainOrderId: Long,
    @SerializedName("deliverDate") val deliverDate: Long,
    @SerializedName("billList") val billList: List<Bill>?,
    @SerializedName("borrowDurationFrom") val borrowDurationFrom: Long,
    @SerializedName("borrowDurationTo") val borrowDurationTo: Long,
    @SerializedName("applyDate") val applyDate: Long,
    @SerializedName("amount") val amount: BigDecimal,
    @SerializedName("assetCode") val assetCode: String,
    @SerializedName("loanWay") val loanWay: String,
    @SerializedName("status") val status: LoanStatus,
    @SerializedName("borrowerAddress") val borrowerAddress: String
) : Serializable {

    fun getOrderIdStr() = chainOrderId.toString()

    fun getStageCountStr() = (this.billList?.size?:0).toString()

    fun isSettlementComplete(): Boolean {
        return status == LoanStatus.REPAID
    }

    fun isFromOtherAddress(): Boolean {
        return billList == null
    }

    data class Bill(
        @SerializedName("actualRepayDate") val actualRepayDate: Long?,
        @SerializedName("amount") val amount: BigDecimal,
        @SerializedName("expectRepayDate") val expectRepayDate: Long,
        @SerializedName("number") val number: String?,
        @SerializedName("status") val status: BillStatus
    ) : Serializable {
        enum class BillStatus : Serializable {
            //待还款
            WAITING_VERIFY,
            //已结清
            VERIFIED,
            //已创建
            CREATED,
            //已清偿
            PAY_OFF,
            //已废弃
            CANCELED
        }
    }
}