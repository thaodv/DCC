package io.wexchain.dccchainservice.domain.telegram

import com.google.gson.annotations.SerializedName

/**
 * @author Created by Wangpeng on 2019/4/2 18:00.
 * usage:
 */
data class GetTelegramUserBean(
        @SerializedName("enableEntrust") val enableEntrust: Boolean,
        @SerializedName("userName") val userName: String?
) {
}
