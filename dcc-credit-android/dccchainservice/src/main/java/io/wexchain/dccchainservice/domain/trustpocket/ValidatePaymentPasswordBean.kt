package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/4 15:49.
 * usage:
 */
data class ValidatePaymentPasswordBean(
        @SerializedName("remainValidateTimes") val remainValidateTimes: String,
        @SerializedName("result") val result: Status) {
    enum class Status {
        SKIPPED, //"已跳过"
        PASSED,  //"已通过"
        REJECTED,//"已拒绝
        LOCKED   //"已锁定"
    }
}
