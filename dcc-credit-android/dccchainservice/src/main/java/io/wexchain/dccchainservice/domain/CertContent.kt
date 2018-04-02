package io.wexchain.dccchainservice.domain

data class CertContent(
    @JvmField val digest1: String,
    @JvmField val digest2: String,
    @JvmField val expired: Long
)