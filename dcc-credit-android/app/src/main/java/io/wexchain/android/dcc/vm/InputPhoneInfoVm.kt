package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.tools.isPhoneNumValid

class InputPhoneInfoVm:ViewModel(){
    val phoneNo = ObservableField<String>()
    val servicePassword = ObservableField<String>()

    val submitEvent = SingleLiveEvent<Pair<String,String>>()

    fun submit(){
        val phone = phoneNo.get()
        val pw = servicePassword.get()
        if(isPhoneNumValid(phone) && !pw.isNullOrBlank()){
            submitEvent.value = phone!! to pw!!
        }
    }

    fun clear() {
        phoneNo.set("")
        servicePassword.set("")
    }
}