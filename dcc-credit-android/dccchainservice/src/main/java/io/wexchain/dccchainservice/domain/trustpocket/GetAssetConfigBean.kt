package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/4/18 20:45.
 * usage:
 */
data class GetAssetConfigBean(
        @SerializedName("digit") val digit: Int) {
}
