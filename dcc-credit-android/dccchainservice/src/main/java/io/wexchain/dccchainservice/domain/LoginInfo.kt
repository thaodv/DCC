package io.wexchain.dccchainservice.domain

/**
 *Created by liuyang on 2018/11/5.
 */
data class LoginInfo(
        val id: Int,
        val inviteId: String?,
        val name: String?,
        val profilePhoto: String?,
        val status: String,
        val identityList: List<IdentityList>?
) {
    data class IdentityList(
            val id: Int,
            val memberId: Int,
            val type: String,
            val identity: String
    )
}