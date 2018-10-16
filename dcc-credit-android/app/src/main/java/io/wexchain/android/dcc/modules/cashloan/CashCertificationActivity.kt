package io.wexchain.android.dcc.modules.cashloan

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.BankCardCertificationActivity
import io.wexchain.android.dcc.IdCertificationActivity
import io.wexchain.android.dcc.SubmitBankCardActivity
import io.wexchain.android.dcc.SubmitIdActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.vm.AuthenticationStatusVm
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCashCertificationBinding
import worhavah.tongniucertmodule.SubmitTNLogActivity
import worhavah.tongniucertmodule.TnLogCertificationActivity

/**
 *Created by liuyang on 2018/10/15.
 */
class CashCertificationActivity : BindActivity<ActivityCashCertificationBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_cash_certification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
    }

    private fun initVm() {
        binding.asIdVm = obtainAuthStatus(CertificationType.ID)
        binding.asBankVm = obtainAuthStatus(CertificationType.BANK)
        binding.asTongniuVm = obtainAuthStatus(CertificationType.TONGNIU)
    }

    private fun obtainAuthStatus(certificationType: CertificationType): AuthenticationStatusVm? {
        return ViewModelProviders.of(this)[certificationType.name, AuthenticationStatusVm::class.java]
                .apply {
                    title.set(getCertTypeTitle(certificationType))
                    authDetail.set(getDescription(certificationType))
                    this.status.set(CertOperations.getCertStatus(certificationType))
                    this.certificationType.set(certificationType)
                    this.performOperationEvent.observe(this@CashCertificationActivity, Observer {
                        it?.let {
                            val type = it.first
                            val certStatus = it.second
                            if (type != null && certStatus != null) {
                                this@CashCertificationActivity.performOperation(type, certStatus)
                            }
                        }
                    })
                }
    }


    private fun getCertTypeTitle(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.id_verification)
            CertificationType.BANK -> getString(R.string.bank_account_verification)
            CertificationType.TONGNIU -> "同牛运营商认证"
            else -> ""
        }
    }

    private fun getDescription(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.verify_your_legal_documentation)
            CertificationType.BANK -> getString(R.string.for_quick_approvalto_improve)
            CertificationType.TONGNIU -> "用于现金借贷审核"
            else -> ""
        }
    }

    private fun performOperation(certificationType: CertificationType, status: UserCertStatus) {
        when (certificationType) {
            CertificationType.ID -> {
                if (status == UserCertStatus.DONE) {
                    navigateTo(IdCertificationActivity::class.java)
                } else {
                    navigateTo(SubmitIdActivity::class.java)
                }
            }

            CertificationType.BANK ->
                if (status == UserCertStatus.DONE) {
                    navigateTo(BankCardCertificationActivity::class.java)
                } else {
                    PassportOperations.ensureCaValidity(this) {
                        navigateTo(SubmitBankCardActivity::class.java)
                    }
                }
            CertificationType.TONGNIU -> {
                when (status) {
                    UserCertStatus.DONE, UserCertStatus.TIMEOUT -> {
                        // navigateTo(TnLogCertificationActivity::class.java)
                        startActivity(Intent(this, TnLogCertificationActivity::class.java))
                    }
                    UserCertStatus.NONE -> {
                        PassportOperations.ensureCaValidity(this) {
                            startActivity(Intent(this, SubmitTNLogActivity::class.java))
                        }
                    }
                    UserCertStatus.INCOMPLETE -> {
                        // get report processing
                        //      startActivity(Intent(this, SubmitTNLogActivity::class.java))
                    }
                }

            }
        }
    }


}