package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField

class AuthenticationStatusVm:ViewModel() {
    val title = ObservableField<String>()
    val status = ObservableField<String>()
    val operation = ObservableField<String>()
    val authDetail = ObservableField<String>()
    val authProvider = ObservableField<String>()
    val authType = ObservableField<AuthType>()
}