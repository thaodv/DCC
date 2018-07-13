package io.wexchain.dccchainservice.domain



data class CheckUpgrade(
    val versionNumber: String,
    val updateLog: String,
    val updateUrl: String,
    val releaseTime: String,
    val mandatoryUpgrade: Boolean
)