package io.wexchain.android.dcc.modules.home.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent

/**
 *Created by liuyang on 2018/11/19.
 */
class LoanHeaderVm : ViewModel() {

    val isShow=ObservableField<Boolean>()
            .apply { set(false) }

    val orderNum=ObservableField<String>()

    val loanNum=ObservableField<String>()

    val loanTime=ObservableField<String>()

    val currency=ObservableField<String>()

    val repay=ObservableField<String>()

    val status=ObservableField<String>()

    val moreOrder=SingleLiveEvent<Void>()
    val orderClick=SingleLiveEvent<Void>()

    fun moreOrder(){
        moreOrder.call()
    }

    fun orderClick(){
        orderClick.call()
    }

}