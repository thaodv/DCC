package io.wexchain.android.dcc.modules.ipfs.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent

/**
 *Created by liuyang on 2018/8/23.
 */
class MyCloudVm : ViewModel() {

    val syncCall = SingleLiveEvent<Void>()
    val resetCall = SingleLiveEvent<Void>()
    val tipsCall = SingleLiveEvent<Void>()
    val isEnable = ObservableField<Boolean>()
            .apply { set(false) }

    fun syncClick() {
        syncCall.call()
    }

    fun resetClick() {
        resetCall.call()
    }

    fun tipsClick() {
        tipsCall.call()
    }



}