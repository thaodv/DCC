package io.wexchain.dccchainservice.domain

enum class MortgageStatus (
        val description:String
){
    REPAID("已还款"),
    INVALID("无效"),
    DELIVERIED("已放款"),
    VIOLATED("已逾期")
}