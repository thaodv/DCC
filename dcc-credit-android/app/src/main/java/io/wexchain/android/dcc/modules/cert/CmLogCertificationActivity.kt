package io.wexchain.android.dcc.modules.cert

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.CertOperations.saveCmLogCertExpired
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.vm.CertificationDataVm
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCertificationDataCmBinding
import io.wexchain.dccchainservice.ChainGateway

class CmLogCertificationActivity : BindActivity<ActivityCertificationDataCmBinding>() {
    override val contentLayoutId: Int = R.layout.activity_certification_data_cm

    internal lateinit var vm: CertificationDataVm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        val data = CertOperations.getCertIdData()
        val phoneNo = CertOperations.getCmLogPhoneNo()!!
        vm = getViewModel<CertificationDataVm>().apply {
            this.title1.set(getString(R.string.phone_num))
            this.title2.set(getString(R.string.phone_owner_name))
            this.title3.set(getString(R.string.cert_expired_label))
            this.value1.set(phoneNo)
            this.value2.set(data?.name ?: "")
            // this.value3.set(ViewModelHelper.expiredText(data.expired))
        }

        vm.renewEvent.observe(this, Observer {
            PassportOperations.ensureCaValidity(this) {
                navigateTo(SubmitCommunicationLogActivity::class.java)
            }
        })
        vm.showDataEvent.observe(this, Observer {
            navigateTo(CmCertDataActivity::class.java) {
                putExtra(Extras.EXTRA_CERT_ORDER_ID, CertOperations.getCmCertOrderId())
            }
        })
        binding.vm = vm
    }

    override fun onResume() {
        super.onResume()
        App.get().chainGateway.getCertData(App.get().passportRepository.currPassport.value!!.address, ChainGateway.BUSINESS_COMMUNICATION_LOG)
                .checkonMain()
                .subscribe({
                    it.content.let {
                        it.let {
                            if (0L != it!!.expired) {
                                saveCmLogCertExpired(it!!.expired)
                            }
                        }
                    }
                    vm.value3.set(it.content.let { it.let { ViewModelHelper.expiredText(it!!.expired) } })
                }, {})
    }

}
