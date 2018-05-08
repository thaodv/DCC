package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.AuthenticationStatusVm
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemCardAuthStatusRequisiteBinding
import io.wexchain.dccchainservice.ChainGateway

class RequisiteCertListActivity : BaseCompatActivity() {

    private val requisiteCerts
        get() = intent.getStringArrayExtra(Extras.EXTRA_REQUISITE_CERTS)

    private val adapter = SimpleDataBindAdapter<ItemCardAuthStatusRequisiteBinding, AuthenticationStatusVm>(
        layoutId = R.layout.item_card_auth_status_requisite,
        variableId = BR.vm
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requisite_cert_list)
        initToolbar()
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
        findViewById<View>(R.id.btn_continue_loan).setOnClickListener {
            finish()
        }
        loadCertStatus()
    }

    private fun loadCertStatus() {
        val certs = requisiteCerts
        if (certs == null || certs.isEmpty()) {
            toast("所需认证列表为空")
        } else {
            val vmList = certs.mapNotNull {
                when (it) {
                    ChainGateway.BUSINESS_ID -> CertificationType.ID
                    ChainGateway.BUSINESS_BANK_CARD -> CertificationType.BANK
                    ChainGateway.BUSINESS_COMMUNICATION_LOG -> CertificationType.MOBILE
                    else -> null
                }
            }.map {
                obtainAuthStatus(it)
            }
            adapter.setList(vmList)
        }
    }

    private fun obtainAuthStatus(certificationType: CertificationType): AuthenticationStatusVm {
        return ViewModelProviders.of(this)[certificationType.name, AuthenticationStatusVm::class.java]
            .apply {
                title.set(getCertTypeTitle(certificationType))
                this.status.set(CertOperations.getCertStatus(certificationType))
                this.certificationType.set(certificationType)
                this.performOperationEvent.observe(this@RequisiteCertListActivity, Observer {
                    it?.let {
                        val type = it.first
                        val certStatus = it.second
                        if (type != null && certStatus != null) {
                            this@RequisiteCertListActivity.performOperation(type, certStatus)
                        }
                    }
                })
            }
    }

    private fun performOperation(type: CertificationType, status: UserCertStatus) {
        when (type) {
            CertificationType.ID -> {
                if (status == UserCertStatus.DONE) {
//                    navigateTo(IdCertificationActivity::class.java)
                } else {
                    PassportOperations.ensureCaValidity(this) {
                        navigateTo(SubmitIdActivity::class.java)
                    }
                }
            }
            CertificationType.PERSONAL -> {
//                navigateTo(SubmitPersonalInfoActivity::class.java)
            }
            CertificationType.BANK ->
                if (status == UserCertStatus.DONE) {
//                    navigateTo(BankCardCertificationActivity::class.java)
                } else {
                    PassportOperations.ensureCaValidity(this) {
                        navigateTo(SubmitBankCardActivity::class.java)
                    }
                }
            CertificationType.MOBILE -> {
                when (status) {
                    UserCertStatus.DONE -> {
//                        navigateTo(CmLogCertificationActivity::class.java)
                    }
                    UserCertStatus.NONE -> {
                        PassportOperations.ensureCaValidity(this) {
                            navigateTo(SubmitCommunicationLogActivity::class.java)
                        }
                    }
                    UserCertStatus.INCOMPLETE -> {
                        // get report processing
                    }
                }
            }
        }
    }

    private fun getCertTypeTitle(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> "身份证认证"
            CertificationType.PERSONAL -> "真实信息认证"
            CertificationType.BANK -> "银行卡认证"
            CertificationType.MOBILE -> "运营商认证"
        }
    }
}
