package io.wexchain.android.dcc.vm.domain

import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.vm.setSelfScale
import io.wexchain.dccchainservice.domain.LoanProduct
import java.math.BigDecimal

data class LoanScratch (
    val product: LoanProduct,
    val amount:BigDecimal,
    val fee:BigDecimal,
    val period:LoanProduct.LoanPeriod,
    val beneficiaryAddress: BeneficiaryAddress,
    val createTime:Long=System.currentTimeMillis()
){
    fun getAmountStr(): String {
        return "${amount.setSelfScale(4)} ${product.currency.symbol}"
    }

}
