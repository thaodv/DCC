package io.wexchain.dccchainservice.domain

enum class LoanPeriodTimeUnit {
    DAY,
    MONTH,
    YEAR
    ;

    fun str(): String {
        return when (this) {
            LoanPeriodTimeUnit.DAY -> "天"
            LoanPeriodTimeUnit.MONTH -> "月"
            LoanPeriodTimeUnit.YEAR -> "年"
        }
    }
}