package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField

class VerifyBankCardSmsCodeVm:ViewModel(){
    val phoneNo = ObservableField<String>()

    val code = ObservableField<String>()
}