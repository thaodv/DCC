package io.wexchain.dccchainservice.domain

import com.google.gson.annotations.SerializedName
import io.wexchain.dccchainservice.ChainGateway
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode


data class LoanProduct(
    @SerializedName("id")
    val id: Long,
    @SerializedName("currency")
    val currency: Currency,
    @SerializedName("dccFeeScope")
    val dccFeeScope: List<Int>,
    @SerializedName("description")
    val description: String,
    @SerializedName("volumeOptionList")
    val volumeOptionList: List<BigInteger>,//represent as raw uint256 value
    @SerializedName("loanRate")
    val loanRate: BigDecimal,
    @SerializedName("loanPeriodList")
    val loanPeriodList: List<LoanPeriod>,
    @SerializedName("lender")
    val lender: Lender,
    @SerializedName("requisiteCertList")
    val requisiteCertList: List<String>,
    @SerializedName("repayPermit")
    val repayPermit: Boolean,
    @SerializedName("repayAheadRate")
    val repayAheadRate: BigDecimal
):Serializable {
    fun getPeriodStr(): String {
        require(loanPeriodList.size >= 2)
        val start = loanPeriodList.first()
        val end = loanPeriodList.last()
        return "${start.str()}-${end.str()}"
    }

    fun getPeriod(index: Int): String? {
        return loanPeriodList.getOrNull(index)?.str()
    }

    fun getVolume(index:Int): String? {
        return volumeOptionList.getOrNull(index)?.toString()
    }

    fun getVolumeRangeStr(): String {
        require(volumeOptionList.isNotEmpty())
        val c = currency
        val start = volumeOptionList.first()
        val end = volumeOptionList.last()
        val startStr = c.convertToDecimal(start).setScale(4,RoundingMode.DOWN).toPlainString()
        val endStr = c.convertToDecimal(end).setScale(4,RoundingMode.DOWN).toPlainString()
        return "$startStr${c.symbol}-$endStr${c.symbol}"
    }

    fun getInterestRateStr(): String {
        val rateStr = loanRate.scaleByPowerOfTen(2).toPlainString()//x100
        return "年化$rateStr%"
    }

    fun getRepayAheadRateStr():String{
        val rateStr = repayAheadRate.scaleByPowerOfTen(2).toPlainString()//x100
        return "年化$rateStr%"
    }

    fun getRequisiteStr():String{
        return requisiteCertList.withIndex().joinToString(separator = "\n") { "${it.index+1}.${certTypeStr(it.value)};" }
    }

    fun getStandardDccFeeStr():String{
        return dccFeeScope.firstOrNull()?.toString()?:""
    }
    fun getPriorityDccFeeStr():String{
        return dccFeeScope.lastOrNull()?.toString()?:""
    }

    private fun certTypeStr(cert:String):String{
        return when(cert){
            ChainGateway.BUSINESS_ID -> "身份证认证"
            ChainGateway.BUSINESS_BANK_CARD -> "银行卡认证"
            ChainGateway.BUSINESS_COMMUNICATION_LOG -> "运营商认证"
            else->"--"
        }
    }

    data class Currency(
        @SerializedName("symbol")
        val symbol: String,
        @SerializedName("decimal")
        val decimal: Int
    ):Serializable {
        fun convertToDecimal(value:BigInteger): BigDecimal {
            return value.toBigDecimal().scaleByPowerOfTen(-decimal)
        }
    }

    data class LoanPeriod(
        @SerializedName("unit")
        val unit: PeriodTimeUnit,
        @SerializedName("value")
        val value: Int
    ):Serializable {
        fun str():String{
            return "$value${unit.str()}"
        }
    }

    enum class PeriodTimeUnit{
        DAY,
        MONTH,
        YEAR
        ;

        fun str():String{
            return when(this){
                LoanProduct.PeriodTimeUnit.DAY -> "天"
                LoanProduct.PeriodTimeUnit.MONTH -> "月"
                LoanProduct.PeriodTimeUnit.YEAR -> "年"
            }
        }
    }

    data class Lender(
        @SerializedName("code")
        val code: String,
        @SerializedName("logoUrl")
        val logoUrl: String,
        @SerializedName("defaultConfig")
        val defaultConfig: Boolean
    ):Serializable
}