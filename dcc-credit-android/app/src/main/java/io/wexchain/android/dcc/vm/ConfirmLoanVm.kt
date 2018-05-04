package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.dccchainservice.domain.LoanProduct
import java.math.BigDecimal

class ConfirmLoanVm:ViewModel() {
    val product = ObservableField<LoanProduct>()

    val amount = ObservableField<BigDecimal>()

    val fee = ObservableField<BigDecimal>()

    val holding = ObservableField<BigDecimal>()
}