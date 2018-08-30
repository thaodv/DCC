package io.wexchain.dccchainservice.domain

/**
 *Created by liuyang on 2018/8/27.
 */

data class IpfsVersion(
    val Version: String,
    val Commit: String,
    val Repo: String,
    val System: String,
    val Golang: String
)