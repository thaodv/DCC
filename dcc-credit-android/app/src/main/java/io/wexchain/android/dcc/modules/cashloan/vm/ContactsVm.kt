package io.wexchain.android.dcc.modules.cashloan.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.common.onTextChanged

/**
 *Created by liuyang on 2018/12/4.
 */
class ContactsVm : ViewModel() {

    val title = ObservableField<String>()

    val relation = ObservableField<String>()
            .apply { set("请选择") }

    val phone = ObservableField<String>()
    val olderPhone = ObservableField<String>()

    val name = ObservableField<String>()
    val olderName = ObservableField<String>()

    val relationCall = SingleLiveEvent<Void>()

    fun selectRelation() {
        relationCall.call()
    }

    val phoneWatcher = onTextChanged {
        phone.set(it)
    }

    val nameWatcher = onTextChanged {
        name.set(it)
    }

    fun check(): Boolean {
        return relation.get() != "请选择" && !phone.get().isNullOrEmpty() && !name.get().isNullOrEmpty()
    }

}