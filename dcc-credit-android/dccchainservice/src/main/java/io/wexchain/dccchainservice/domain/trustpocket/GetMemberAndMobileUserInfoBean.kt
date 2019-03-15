package io.wexchain.dccchainservice.domain.trustpocket

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/3/13 15:59.
 * usage:
 */
data class GetMemberAndMobileUserInfoBean(
        @SerializedName("mobileUserId") val mobileUserId: String,
        @SerializedName("memberId") val memberId: String,
        @SerializedName("photoUrl") val photoUrl: String,
        @SerializedName("nickName") val nickName: String,
        @SerializedName("mobile") val mobile: String,
        @SerializedName("address") val address: String
) {}
