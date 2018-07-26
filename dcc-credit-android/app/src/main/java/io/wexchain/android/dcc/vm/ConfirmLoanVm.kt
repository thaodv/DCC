package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.vm.domain.LoanScratch
import java.math.BigDecimal

class ConfirmLoanVm : ViewModel() {

    val scratch = ObservableField<LoanScratch>()

    val holding = ObservableField<BigDecimal>()

    val interest = ObservableField<BigDecimal>()

    val totalAmount = ObservableField<BigDecimal>()

    fun setScratch(loanScratch: LoanScratch) {
        this.scratch.set(loanScratch)
        updateInterest()
    }

    private fun updateInterest() {
        val loanScratch = this.scratch.get()
        loanScratch?.let {
            App.get().scfApi.getLoanInterest(it.product.id, it.amount.toPlainString(), it.period.value)
                    .checkonMain()
                    .subscribeBy {
                        if (scratch.get() == loanScratch) {
                            this.interest.set(it)
                            this.totalAmount.set(it + loanScratch.amount)
                        }
                    }
        }
    }

    fun loadHolding() {
        ScfOperations.loadHolding()
                .subscribe { value ->
                    holding.set(value)
                }
    }
}