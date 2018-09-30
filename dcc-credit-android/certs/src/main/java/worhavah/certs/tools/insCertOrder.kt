package worhavah.certs.tools
import io.wexchain.dccchainservice.domain.CertStatus
import io.wexchain.dccchainservice.domain.CertContent

/**
 * Created by sisel on 2018/2/9.
 */

data class insCertOrder(
        @JvmField val orderId: Long,
        @JvmField val applicant: String,
        @JvmField val status: CertStatus,
        @JvmField val content: CertContent,
        @JvmField val fee: Long?
) {

}