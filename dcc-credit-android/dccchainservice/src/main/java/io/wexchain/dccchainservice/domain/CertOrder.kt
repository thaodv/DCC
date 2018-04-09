package io.wexchain.dccchainservice.domain

/**
 * Created by sisel on 2018/2/9.
 */

data class CertOrder(
        @JvmField val orderId: Long,
        @JvmField val applicant: String,
        @JvmField val status: CertStatus,
        @JvmField val content: CertContent,
        @JvmField val feeDCC: Long?
) {

}