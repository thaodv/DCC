package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.vm.domain.LoanScratch
import io.wexchain.dccchainservice.domain.LoanProduct
import java.math.BigDecimal

class ConfirmLoanVm : ViewModel() {

    val scratch = ObservableField<LoanScratch>()

    val holding = ObservableField<BigDecimal>()

    fun loadHolding() {
        ScfOperations.loadHolding()
            .subscribe { value ->
                holding.set(value)
            }
    }
}