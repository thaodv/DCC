package io.wexchain.dccchainservice.type

/**
 *Created by liuyang on 2018/12/5.
 */
enum class TnOrderStatus {
    NONE,//未申请
    CREATED,// 已创建
    AUDITING,// 审核中
    AUDITED,//已审核
    APPLIED,//已申请
    ACCEPTED,// 已接受
    DELIVERING,//放款中
    DELIVERIED,// 已放款
    DELAYED,//已逾期
    REPAID,// 已还款
    FAILURE,//放款失败
    CANCELLED,//已撤销
    REJECTED// 已拒绝
}