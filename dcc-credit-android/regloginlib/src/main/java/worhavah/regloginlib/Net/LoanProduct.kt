package worhavah.regloginlib.Net

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode


data class LoanProduct(
    @SerializedName("id")
    val id: Long,
    @SerializedName("currency")
    val currency: LoanCurrency,
    @SerializedName("dccFeeScope")
    val dccFeeScope: List<Int>,
    @SerializedName("description")
    val description: String,
    @SerializedName("volumeOptionList")
    val volumeOptionList: List<BigDecimal>,//represent as raw uint256 value
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
    val repayAheadRate: BigDecimal,
    @SerializedName("name")
    val name: String?,
    @SerializedName("logoUrl")
    val logoUrl: String?,
    @SerializedName("agreementTemplateUrl")
    val agreementTemplateUrl: String?,
    /**
     * 期数
     */
    @SerializedName("repayCyclesNo")
    val repayCyclesNo: Long,
    /**
     * 借款类型
     */
    @SerializedName("loanType")
    val loanType: String
) : Serializable {
    fun getPeriodStr(): String {
        require(loanPeriodList.size >= 2)
        val start = loanPeriodList.first()
        val end = loanPeriodList.last()
        return "${start.value}-${end.str()}"
    }

    fun getPeriod(index: Int): String? {
        return loanPeriodList.getOrNull(index)?.str()
    }

    fun getVolume(index: Int): String? {
        return volumeOptionList.getOrNull(index)?.run {
            this.setScale(4, RoundingMode.DOWN).toPlainString()
        }
    }

    fun getVolumeRangeStr(): String {
        require(volumeOptionList.isNotEmpty())
        val c = currency
        val start = volumeOptionList.first()
        val end = volumeOptionList.last()
        val startStr = start.toPlainString()
        val endStr = end.toPlainString()
        return "$startStr-$endStr${c.symbol}"
    }

    fun getInterestRateStr(): String {
        val rateStr = loanRate.divide(BigDecimal("3.65"), RoundingMode.DOWN).setScale(2,RoundingMode.DOWN).toPlainString()//interest rate in day
        return "$rateStr%"
    }

    fun getRepayAheadRateStr(): String {
        val rateStr = repayAheadRate.scaleByPowerOfTen(2).toPlainString()
        return "$rateStr%"
    }

    fun getRequisiteStr(): String {
        return requisiteCertList.withIndex()
            .joinToString(separator = "\n") { "${it.index + 1}.${certTypeStr(it.value)};" }
    }

    fun getStandardDccFeeStr(): String {
        return dccFeeScope.firstOrNull()?.toString() ?: ""
    }

    fun getPriorityDccFeeStr(): String {
        return dccFeeScope.lastOrNull()?.toString() ?: ""
    }

    fun getFeeRange(): Int {
        return dccFeeScope.last() - dccFeeScope.first()
    }

    private fun certTypeStr(cert: String): String {
        return when (cert) {
            ChainGateway.BUSINESS_ID -> "身份证认证"
            ChainGateway.BUSINESS_BANK_CARD -> "银行卡认证"
            ChainGateway.BUSINESS_COMMUNICATION_LOG -> "运营商认证"
            else -> "--"
        }
    }

    data class LoanPeriod(
        @SerializedName("unit")
        val unit: LoanPeriodTimeUnit,
        @SerializedName("value")
        val value: Int
    ) : Serializable {
        fun str(): String {
            return "$value${unit.str()}"
        }
    }

}