package io.wexchain.dccchainservice.type

/**
 *Created by liuyang on 2018/12/5.
 */
enum class TnOrderStatus {
    NONE,
    CREATED,
    AUDITING,
    AUDITED,
    APPLIED,
    ACCEPTED,
    DELIVERING,
    DELIVERIED,
    DELAYED,
    REPAID,
    FAILURE,
    CANCELLED,
    REJECTED
}