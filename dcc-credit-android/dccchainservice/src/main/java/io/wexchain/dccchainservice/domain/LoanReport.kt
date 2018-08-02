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
    @SerializedName("borrowerAddress") val borrowerAddress: String,

    @SerializedName("mortgageAmount") val mortgageAmount: BigDecimal,
    @SerializedName("mortgageUnit") val mortgageUnit: String,
    @SerializedName("mortgageStatus") val mortgageStatus: MortgageStatus,
    @SerializedName("loanType") val loanType: LoanType



) : Serializable {

    fun getOrderIdStr() = chainOrderId.toString()

    fun getStageCountStr() = (this.billList?.size?:0).toString()

    fun isSettlementComplete(): Boolean {
        if(isMort()){
            return mortgageStatus == MortgageStatus.REPAID
        }else{
            return status == LoanStatus.REPAID
        }
    }

    fun isFromOtherAddress(): Boolean {
        return billList == null
    }
    fun isMort():Boolean{
        return loanType == LoanType.MORTGAGE
    }
    fun titleinfo():String{
        return if(isMort()){
            "订单信息"
        }else{
            "借币信息"
        }
    }
    fun titleinfo2():String{
        return if(isMort()){
            "还款账单"
        }else{
            "还币信息"
        }
    }
    fun shouldlong( le:Long):Long{
        var l:Long=0L
        if(null!=le) l=le
        return l
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