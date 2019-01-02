package io.wexchain.android.dcc.modules.cert

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.modules.cashloan.act.CashCertificationActivity
import io.wexchain.android.dcc.vm.CertificationDataVm
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCertificationDataBinding

class IdCertificationActivity : BindActivity<ActivityCertificationDataBinding>() {

    override val contentLayoutId: Int = R.layout.activity_certification_data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        val type = intent.getStringExtra("type")
        if (CashCertificationActivity.CERT_TYPE_CASHLOAN == type) {
            binding.tvCertOrg.text = "认证方:同牛"
        }
        val data = CertOperations.getCertIdData()!!
        val certIdPics = CertOperations.getCertIdPics()!!
        val vm = getViewModel<CertificationDataVm>().apply {
            this.title1.set(getString(R.string.name_label))
            this.title2.set(getString(R.string.id_no_label))
            this.title3.set(getString(R.string.cert_expired_label))
            this.value1.set(data.name)
            this.value2.set(data.id)
            this.value3.set(ViewModelHelper.expiredText(data.expired))
            this.imgFront.set(certIdPics.first)
            this.imgBack.set(certIdPics.second)
            this.imgPhoto.set(certIdPics.third)
        }
        vm.renewEvent.observe(this, Observer {
            PassportOperations.ensureCaValidity(this) {
                navigateTo(SubmitIdActivity::class.java)
            }
        })
        binding.vm = vm
    }
}
