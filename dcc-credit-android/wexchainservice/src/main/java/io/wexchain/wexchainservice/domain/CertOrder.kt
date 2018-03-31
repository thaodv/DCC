package io.wexchain.wexchainservice.domain

/**
 * Created by sisel on 2018/2/9.
 */

data class CertOrder(
    @JvmField val orderId: Long,
    @JvmField val applicant: String,
    @JvmField val status: Status,
    @JvmField val content: Content
) {
    data class Content(
        @JvmField val digest1: String,
        @JvmField val digest2: String,
        @JvmField val expired: Long
    )

    enum class Status {
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
}