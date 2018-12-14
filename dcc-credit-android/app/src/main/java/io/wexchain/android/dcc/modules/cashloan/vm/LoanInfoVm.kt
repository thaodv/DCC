package io.wexchain.android.dcc.modules.cashloan.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.dccchainservice.domain.TnLoanOrder
import io.wexchain.ipfs.utils.io_main

/**
 *Created by liuyang on 2018/12/11.
 */
class LoanInfoVm : ViewModel() {

    val order = ObservableField<TnLoanOrder>()
    val loanAgreement = SingleLiveEvent<Void>()
    val confirm = SingleLiveEvent<Void>()


    fun loanAgreement(){
        loanAgreement.call()
    }
    fun confirm(){
        confirm.call()
    }

}