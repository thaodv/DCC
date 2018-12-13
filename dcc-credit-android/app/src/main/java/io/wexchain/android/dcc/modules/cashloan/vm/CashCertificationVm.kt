package io.wexchain.android.dcc.modules.cashloan.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent

/**
 *Created by liuyang on 2018/12/11.
 */
class CashCertificationVm : ViewModel() {

    val btnStatus = ObservableField<Boolean>()
    val isCert = ObservableField<Boolean>()

    val userinfoCall = SingleLiveEvent<Void>()
    val tipsCall = SingleLiveEvent<Void>()
    val commitCall = SingleLiveEvent<Void>()

    fun commitCert() {
        commitCall.call()
    }

    fun userinfoCert() {
        userinfoCall.call()
    }

    fun showTips() {
        tipsCall.call()
    }
}