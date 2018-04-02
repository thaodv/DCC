package io.wexchain.dccchainservice.domain


/**
 * Created by lulingzhi on 2017/11/15.
 */
data class TicketResponse(
        @JvmField val accessRestriction: AccessRestriction,
        @JvmField val ticket: String,
        @JvmField val image: String?,
        @JvmField var answer: String?,
        @JvmField val expiredSeconds: Long?
) {
    enum class AccessRestriction {
        OPENED, CAPTCHA, REJECTED, GRANTED
        ;

        fun answerNotRequired(): Boolean {
            return this == OPENED || this == GRANTED
        }

        fun answerRequired(): Boolean {
            return this == CAPTCHA
        }
    }

}