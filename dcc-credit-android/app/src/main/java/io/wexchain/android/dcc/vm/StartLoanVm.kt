package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.vm.domain.LoanScratch
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.LoanProduct
import java.math.BigDecimal

class StartLoanVm : ViewModel() {
    val product = ObservableField<LoanProduct>()

    val volSelectIndex = ObservableInt(1)

    val inputCustomVol = ObservableField<String>()

    val periodSelectIndex = ObservableInt(1)

    val dccBalance = ObservableField<BigDecimal>()

    val fee = ObservableField<BigDecimal>()

    val feeAdditional = ObservableInt(0)

    val address = ObservableField<BeneficiaryAddress>()

    val feeExceeded = ObservableBoolean(false)

    val completedCert = ObservableField<List<String>>()

    val proceedEvent = SingleLiveEvent<LoanScratch>()

    val failEvent = SingleLiveEvent<String>()

    val refreshEvent = SingleLiveEvent<Void>()

    val volSelChangedEvent = SingleLiveEvent<Int>()

    init {
        feeAdditional.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val product = product.get()
                if (product == null) {
                    fee.set(null)
                } else {
                    val userFee = product.dccFeeScope.first().toBigDecimal() + BigDecimal.valueOf((feeAdditional.get()).toLong()).scaleByPowerOfTen(-2)
                    fee.set(userFee)
                }
            }
        })
        val checkExceed: Observable.OnPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val product = product.get()
                if (product == null) {
                    feeExceeded.set(false)
                } else {
                    val userFee = fee.get()
                    val balance = dccBalance.get()
                    feeExceeded.set(balance != null && userFee != null && userFee > balance)
                }
            }
        }
        fee.addOnPropertyChangedCallback(checkExceed)
        dccBalance.addOnPropertyChangedCallback(checkExceed)
        product.addOnPropertyChangedCallback(checkExceed)
    }

    fun setProduct(p: LoanProduct) {
        this.product.set(p)
        this.feeAdditional.set(0)
        this.fee.set(p.dccFeeScope.first().toBigDecimal())
    }

    fun checkVol(checked: Boolean, index: Int) {
        if (checked) {
            volSelectIndex.set(index)
            refreshEvent.call()
            volSelChangedEvent.value = index
        }
    }

    fun checkPeriod(checked: Boolean, index: Int) {
        if (checked) {
            periodSelectIndex.set(index)
            refreshEvent.call()
        }
    }

    fun loadHolding() {
        ScfOperations.loadHolding()
            .subscribe { value ->
                dccBalance.set(value)
            }
    }

    fun checkAndProceed() {
        val product = this.product.get()
        if (product == null) {
            failEvent.value = "product not exists"
            return
        }
        val fee = this.fee.get()
        val amount = when(volSelectIndex.get()){
            -1 -> inputCustomVol.get()?.toBigDecimal()
            else -> product.volumeOptionList.getOrNull(volSelectIndex.get())?.toBigDecimal()
        }
        if (fee == null || amount == null){
            failEvent.value = "amount or fee is empty"
            return
        }
        val c = product.currency
        val minAmount = c.convertToDecimal(product.volumeOptionList.first())
        val maxAmount = c.convertToDecimal(product.volumeOptionList.last())
        if(amount < minAmount){
            failEvent.value = "输入金额不可小于${minAmount.toPlainString()}"
            return
        }else if(amount > maxAmount){
            failEvent.value = "输入金额不可大于${maxAmount.toPlainString()}"
            return
        }
        val address = this.address.get()
        if (address == null){
            failEvent.value = "beneficiary address is empty"
            return
        }
        val loanPeriod = product.loanPeriodList.getOrNull(periodSelectIndex.get())
        if (loanPeriod == null){
            failEvent.value = "loan period is empty"
            return
        }
        val completed = completedCert.get()?: emptyList()
        val requiredCerts = product.requisiteCertList.filter { !completed.contains(it) }
        if(requiredCerts.isNotEmpty()){
            failEvent.value = requiredCerts.map {
                when (it) {
                    ChainGateway.BUSINESS_ID -> "身份证信息"
                    ChainGateway.BUSINESS_BANK_CARD -> "银行卡信息"
                    ChainGateway.BUSINESS_COMMUNICATION_LOG -> "运营商信息"
                    else -> it
                } + "认证未完成"
            }.first()
            return
        }
        proceedEvent.value = LoanScratch(
            product,
            amount,
            fee,
            loanPeriod,
            address
        )
    }
}