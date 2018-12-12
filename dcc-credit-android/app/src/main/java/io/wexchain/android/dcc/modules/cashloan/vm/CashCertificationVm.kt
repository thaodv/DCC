package io.wexchain.android.dcc.modules.cashloan.vm

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.modules.cashloan.bean.CertificationInfo
import io.wexchain.android.dcc.tools.toBean
import io.wexchain.android.dcc.vm.domain.UserCertStatus

/**
 *Created by liuyang on 2018/12/11.
 */
class CashCertificationVm : ViewModel() {

    val btnStatus = ObservableField<Boolean>()
            .apply {
                val idCertPassed = CertOperations.isIdCertPassed()
                val bankCertPassed = CertOperations.isBankCertPassed()
                val tnstatus = worhavah.certs.tools.CertOperations.getTNLogUserStatus()
                val tnCmStatus = tnstatus == worhavah.certs.tools.UserCertStatus.DONE
                val infoCert = App.get().passportRepository.getUserInfoCert()
                val isCert = infoCert?.let {
                    val data = it.toBean(CertificationInfo::class.java)
                    data.isCert()
                } ?: false
                val status= idCertPassed && bankCertPassed && tnCmStatus && isCert
                set(status)
            }

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