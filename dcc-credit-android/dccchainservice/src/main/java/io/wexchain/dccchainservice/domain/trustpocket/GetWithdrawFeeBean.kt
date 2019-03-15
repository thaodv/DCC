package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/14 14:56.
 * usage:
 */
data class GetWithdrawFeeBean(
        @SerializedName("decimalValue") val decimalValue: String
)
