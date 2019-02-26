package io.wexchain.android.dcc.fragment.home.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import io.wexchain.android.common.SingleLiveEvent

/**
 *Created by liuyang on 2018/10/30.
 */
class ServiceCardVm : ViewModel() {
    val title = ObservableField<String>()
    val name = ObservableField<String>()
    val message = ObservableField<String>()
    val img = ObservableField<Drawable>()

    val showBtn = ObservableField<Boolean>()
    val btnTxt = ObservableField<String>()

    val showTip = ObservableField<Boolean>()

    val performOperationEvent = SingleLiveEvent<Void>()

    fun performOperation() {
        performOperationEvent.call()
    }
}
