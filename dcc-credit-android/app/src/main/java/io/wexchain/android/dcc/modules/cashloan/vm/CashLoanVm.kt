package io.wexchain.android.dcc.modules.cashloan.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.dccchainservice.type.TnOrderStatus

/**
 *Created by liuyang on 2018/12/6.
 */
class CashLoanVm : ViewModel() {

    val loanStatus = ObservableField<TnOrderStatus>()

    val maximumAmount = ObservableField<String>()

    val requestCall=SingleLiveEvent<Void>()

    val loanTipsCall=SingleLiveEvent<Void>()


    fun requestBtn(){
        requestCall.call()
    }

    fun loanTips(){
        loanTipsCall.call()
    }


}