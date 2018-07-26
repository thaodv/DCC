package io.wexchain.dccchainservice.domain

import io.wexchain.dccchainservice.Application
import io.wexchain.dccchainservice.R

enum class LoanType(
        val description: String
) {
    LOAN("普通借贷"),//已失效
    MORTGAGE(Application.getContext()?.getString(R.string.mortgage_loan) ?: "抵押借贷"),//已创建

}