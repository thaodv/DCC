package io.wexchain.android.dcc.modules.cashloan.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.text.Editable
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.modules.cashloan.SimpleTextWatcher

/**
 *Created by liuyang on 2018/12/4.
 */
class ContactsVm : ViewModel() {

    val title = ObservableField<String>()

    val relation = ObservableField<String>()
            .apply { set("请选择") }

    val phone = ObservableField<String>()

    val name = ObservableField<String>()

    val relationCall = SingleLiveEvent<Void>()

    fun selectRelation() {
        relationCall.call()
    }

    val  phoneWatcher = object :SimpleTextWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.toString()?.let {
                phone.set(it)
            }
        }
    }

    val  nameWatcher = object :SimpleTextWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.toString()?.let {
                name.set(it)
            }
        }
    }

}