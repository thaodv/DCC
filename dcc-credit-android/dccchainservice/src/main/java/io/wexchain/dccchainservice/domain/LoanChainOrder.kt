package io.wexchain.dccchainservice.domain

import com.google.gson.annotations.SerializedName
import java.math.BigInteger


data class LoanChainOrder(
    @SerializedName("id") val id: Long,
    @SerializedName("version") val version: Int,
    @SerializedName("borrower") val borrower: String,
    @SerializedName("idHash") val idHash: String,
    @SerializedName("status") val status: LoanStatus,
    @SerializedName("fee") val fee: BigInteger,
    @SerializedName("applicationDigest") val applicationDigest: String,
    @SerializedName("agreementDigest") val agreementDigest: String,
    @SerializedName("repayDigest") val repayDigest: String,
    @SerializedName("receiveAddress") val receiveAddress: String
) {

    fun isNextOrderRestricted(): Boolean {
        return this !== ABSENT_ORDER && (
                this.status == LoanStatus.CREATED || this.status == LoanStatus.AUDITING
                )
    }

    companion object {
        /**
         * dummy order object
         * represent no orders
         */
        val ABSENT_ORDER = LoanChainOrder(
            0L,
            0,
            "",
            "",
            LoanStatus.INVALID,
            BigInteger.ZERO,
            "",
            "",
            "",
            ""
        )
    }

}
