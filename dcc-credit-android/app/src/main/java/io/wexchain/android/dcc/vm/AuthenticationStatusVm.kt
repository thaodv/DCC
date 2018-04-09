package io.wexchain.android.dcc.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.vm.domain.UserCertStatus

class AuthenticationStatusVm:ViewModel() {
    val title = ObservableField<String>()
    val status = ObservableField<UserCertStatus>()
    val authDetail = ObservableField<String>()
    val certificationType = ObservableField<CertificationType>()

    val performOperationEvent= SingleLiveEvent<Pair<CertificationType?,UserCertStatus?>>()

    fun performOperation(){
        performOperationEvent.value = certificationType.get() to status.get()
    }
}