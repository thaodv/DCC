package io.wexchain.dccchainservice.domain

data class CheckUpgrade(
    val version: String,
    val updateLog: String,
    val updateUrl: String,
    val releaseTime: String,
    val mandatoryUpgrade: Boolean
)
