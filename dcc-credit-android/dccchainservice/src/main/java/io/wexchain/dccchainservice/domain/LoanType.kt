package io.wexchain.dccchainservice.domain

import io.wexchain.android.common.BaseApplication
import io.wexchain.dccchainservice.R

enum class LoanType(
        val description: String
) {
    LOAN(BaseApplication.context.getString(R.string.ordinary_loan)),//已失效
    MORTGAGE(BaseApplication.context.getString(R.string.mortgage_loan) ),//已创建

}