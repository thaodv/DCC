package io.wexchain.dccchainservice.domain

/**
 * Created by sisel on 2018/2/9.
 */

data class CertOrder(
        @JvmField val orderId: Long,
        @JvmField val applicant: String,
        @JvmField val status: Status,
        @JvmField val content: CertContent
) {

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