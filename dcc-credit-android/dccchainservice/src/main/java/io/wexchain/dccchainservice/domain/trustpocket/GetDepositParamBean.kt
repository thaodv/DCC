package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/4/3 17:42.
 * usage:
 */
data class GetDepositParamBean(
        @SerializedName("confirmedBlockNumber") val confirmedBlockNumber: String?,
        @SerializedName("depositMinAmt") val depositMinAmt: String
) {
}
