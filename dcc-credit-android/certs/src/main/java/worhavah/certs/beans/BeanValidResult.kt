package worhavah.certs.beans

import java.io.Serializable

/**
 * Created by sisel on 2018/1/16.
 * Ethereum standard transaction
 */
data class BeanValidResult(
        /**
         * tx hash
         */
        @JvmField val phoneNum: String,
        @JvmField val verifyDate: String

) : Serializable