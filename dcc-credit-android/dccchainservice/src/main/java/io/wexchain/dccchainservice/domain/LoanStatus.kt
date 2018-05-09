package io.wexchain.dccchainservice.domain

enum class LoanStatus {
    INVALID,//已失效
    CREATED,//已创建
    CANCELLED,//已撤销
    AUDITING,//审核中
    REJECTED,//拒绝
    APPROVED,//审核通过
    FAILURE,//放款失败
    DELIVERED,//已放款
    RECEIVIED,//已收款
    REPAID,//已还款
}