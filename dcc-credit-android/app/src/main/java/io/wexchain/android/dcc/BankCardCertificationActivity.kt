package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.vm.CertificationDataVm
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityCertificationDataBinding

class BankCardCertificationActivity : BindActivity<ActivityCertificationDataBinding>() {
    override val contentLayoutId: Int = R.layout.activity_certification_data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        val data = CertOperations.getCertBankCardData()!!
        val expired = CertOperations.getBankCardCertExpired()
        val vm = getViewModel<CertificationDataVm>().apply {
            this.title1.set(getString(R.string.issuer_bank))
            this.title2.set(getString(R.string.card_no))
            this.title3.set(getString(R.string.cert_expired_label))
            this.value1.set(data.bankCode)
            this.value2.set(data.bankCardNo)
            this.value3.set(ViewModelHelper.expiredText(expired))
        }
        vm.renewEvent.observe(this, Observer {
            PassportOperations.ensureCaValidity(this) {
                navigateTo(SubmitBankCardActivity::class.java)
            }
        })
        binding.vm = vm
    }
}
