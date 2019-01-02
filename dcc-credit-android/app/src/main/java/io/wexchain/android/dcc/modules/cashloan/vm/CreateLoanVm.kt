package io.wexchain.android.dcc.modules.cashloan.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.dccchainservice.domain.BankInfo

/**
 *Created by liuyang on 2018/12/7.
 */
class CreateLoanVm : ViewModel() {

    val bankCardNum = ObservableField<String>()
            .apply { set("未绑定") }

    val agreement = ObservableField<Boolean>()
            .apply { set(false) }

    val confirmLoan = SingleLiveEvent<Void>()
    val showAgreement = SingleLiveEvent<Void>()
    val showbankCard = SingleLiveEvent<Void>()


    fun checkAgreement() {
        agreement.set(agreement.get()?.let { !it } ?: true)
    }

    fun showAgreement(){
        showAgreement.call()
    }

    fun confirmLoan() {
        confirmLoan.call()
    }

    fun showbankCard(){
        showbankCard.call()
    }


}