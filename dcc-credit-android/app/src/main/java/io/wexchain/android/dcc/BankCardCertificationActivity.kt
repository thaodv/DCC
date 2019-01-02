package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.modules.cashloan.act.CashCertificationActivity
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.vm.CertificationDataVm
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.android.dcc.vm.domain.BankCardInfo
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCertificationDataBinding

class BankCardCertificationActivity : BindActivity<ActivityCertificationDataBinding>() {

    override val contentLayoutId: Int = R.layout.activity_certification_data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        val type = intent.getStringExtra("type")
        if (CashCertificationActivity.CERT_TYPE_CASHLOAN == type) {
            binding.tvCertOrg.text = "认证方:同牛"
        }
        binding.includeIdCard.visibility = View.GONE
        val data = CertOperations.getCertBankCardData()!!
        val expired = CertOperations.getBankCardCertExpired()
        val vm = getViewModel<CertificationDataVm>().apply {
            this.title1.set(getString(R.string.issuer_bank))
            this.title2.set(getString(R.string.card_no))
            this.title3.set(getString(R.string.cert_expired_label))
            // this.value1.set(data.bankCode)
            this.value2.set(data.bankCardNo)
            this.value3.set(ViewModelHelper.expiredText(expired))
        }
        vm.renewEvent.observe(this, Observer {
            PassportOperations.ensureCaValidity(this) {
                navigateTo(SubmitBankCardActivity::class.java)
            }
        })
        binding.vm = vm
        if (data.bankName.isNullOrEmpty()) {
            App.get().marketingApi.getBankList()
                    .checkonMain()
                    .toObservable()
                    .flatMap {
                        Observable.fromIterable(it)
                    }
                    .filter {
                        data.bankCode == it.bankCode
                    }
                    .subscribeBy {
                        vm.value1.set(it.bankName)
                        val info = CertOperations.getCertBankCardData()!!
                        CertOperations.setCertBankCardData(BankCardInfo(info.bankCode, info.bankCardNo, info.phoneNo, it.bankName))
                    }
        } else {
            vm.value1.set(data.bankName)
        }
    }
}
