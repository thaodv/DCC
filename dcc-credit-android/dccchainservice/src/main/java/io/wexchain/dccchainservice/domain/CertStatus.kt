package io.wexchain.dccchainservice.domain

enum class CertStatus {
    APPLIED,
    PASSED,
    REJECTED,
    INVALID,
    REVOKED,
    DISCARDED
    ;

    fun isPassed() = this == PASSED

    fun isFailed() = this == REJECTED
            || this == INVALID
            || this == REVOKED
            || this == DISCARDED
}