package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.vm.CertificationType
import io.wexchain.android.dcc.vm.AuthenticationStatusVm
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityMyCreditBinding

class MyCreditActivity : BindActivity<ActivityMyCreditBinding>() {
    override val contentLayoutId: Int = R.layout.activity_my_credit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.asIdVm = obtainAuthStatus(CertificationType.ID)
        binding.asBankVm = obtainAuthStatus(CertificationType.BANK)
        binding.asMobileVm = obtainAuthStatus(CertificationType.MOBILE)
        binding.asPersonalVm = obtainAuthStatus(CertificationType.PERSONAL)
    }

    private fun obtainAuthStatus(certificationType: CertificationType): AuthenticationStatusVm? {
        return ViewModelProviders.of(this)[certificationType.name, AuthenticationStatusVm::class.java]
                .apply {
                    //todo 
                    title.set(certificationType.name)
                    status.set("TODOTODO")
                    operation.set("更新")
                    authDetail.set("32142514123432\n".repeat(3))
                    authProvider.set("云金融")
                    this.certificationType.set(certificationType)
                    this.performOperationEvent.observe(this@MyCreditActivity, Observer {
                        performOperation(certificationType)
                    })
                }
    }

    private fun performOperation(certificationType: CertificationType) {
        when(certificationType){
            CertificationType.ID ->
                navigateTo(SubmitIdActivity::class.java)
            CertificationType.PERSONAL ->
                navigateTo(SubmitPersonalInfoActivity::class.java)
            CertificationType.BANK ->
                navigateTo(SubmitBankCardActivity::class.java)
            CertificationType.MOBILE -> TODO()
        }
    }
}
