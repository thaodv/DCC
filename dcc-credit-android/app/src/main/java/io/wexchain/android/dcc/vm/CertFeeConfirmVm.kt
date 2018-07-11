package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.chain.ScfOperations

class CertFeeConfirmVm : ViewModel() {

    val fee = ObservableField<String>()

    val holding = ObservableField<String>()

    val confirmEvent = SingleLiveEvent<Void>()

    fun confirm() {
        confirmEvent.call()
    }

    fun loadHolding() {
        ScfOperations.loadHolding()
                .subscribe { value ->
                    holding.set("${value.currencyToDisplayStr()} DCC")
                }
    }
}
