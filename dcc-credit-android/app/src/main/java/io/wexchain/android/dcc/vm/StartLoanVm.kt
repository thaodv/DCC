package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.databinding.ObservableInt
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.dccchainservice.domain.LoanProduct
import java.math.BigDecimal

class StartLoanVm:ViewModel() {
    val product = ObservableField<LoanProduct>()

    val volSelectIndex = ObservableInt(1)

    val inputCustomVol = ObservableField<String>()

    val periodSelectIndex = ObservableInt(1)

    val dccBalance = ObservableField<BigDecimal>()

    val fee = ObservableField<BigDecimal>()

    val address = ObservableField<BeneficiaryAddress>()

    fun checkVol(checked:Boolean,index:Int){
        if(checked){
            volSelectIndex.set(index)
        }
    }
    fun checkPeriod(checked:Boolean,index:Int){
        if(checked){
            periodSelectIndex.set(index)
        }
    }
}