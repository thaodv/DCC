package worhavah.regloginlib.Net.beans

data class CertOrderUpdatedEvent(
        @JvmField val orderId: Long,
        @JvmField val applicant: String,
        @JvmField val status: CertStatus
) {
}