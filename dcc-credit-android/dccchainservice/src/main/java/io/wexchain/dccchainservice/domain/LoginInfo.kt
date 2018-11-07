package io.wexchain.dccchainservice.domain

/**
 *Created by liuyang on 2018/11/5.
 */
data class LoginInfo(
    val member: Member,
    val player: Player
)

data class Member(
    val id: Int,
    val inviteId: Any,
    val name: String,
    val profilePhoto: String,
    val status: String,
    val identityList: List<Identity>
)

data class Identity(
    val id: Int,
    val memberId: Int,
    val type: String,
    val identity: String
)

data class Player(
    val id: Int,
    val nickName: String,
    val portrait: String,
    val unionId: String
)
