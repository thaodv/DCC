package worhavah.tongniucertmodule

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import com.githang.statusbar.StatusBarCompat
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.dccchainservice.ChainGateway
import worhavah.certs.tools.CertOperations
import worhavah.certs.tools.CertOperations.saveTnLogCertExpired
import worhavah.certs.vm.CertificationDataVm
import worhavah.certs.vm.checkonMain
import worhavah.mobilecertmodule.R
import worhavah.mobilecertmodule.databinding.ActivityTncertificationDataCmBinding
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.tools.PassportOperations
import java.text.SimpleDateFormat
import java.util.*

class TnLogCertificationActivity : BindActivity<ActivityTncertificationDataCmBinding>() {
    override val contentLayoutId: Int = R.layout.activity_tncertification_data_cm
    internal lateinit var vm: CertificationDataVm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true,true)
        StatusBarCompat.setStatusBarColor(this,resources.getColor(R.color.white))
        val data = CertOperations.getCertIdData()
        val phoneNo = CertOperations.getTnLogPhoneNo()!!
          vm = getViewModel<CertificationDataVm>().apply {
            this.title1.set(getString(R.string.phone_num))
            this.title2.set(getString(R.string.phone_owner_name))
            this.title3.set(getString(R.string.cert_expired_label))
            this.value1.set(phoneNo)
            this.value2.set(data?.name ?: "")
             // this.value3.set( expiredText(data.expired))
             this.value3.set( expiredText(worhavah.certs.tools.CertOperations.getTNLogCertExpired()))
        }
        vm.renewEvent.observe(this, Observer {
            PassportOperations.ensureCaValidity(this) {
                startActivityForResult( Intent(this, SubmitTNLogActivity::class.java),2333 )
              //  navigateTo(SubmitTNLogActivity::class.java)
            }
        })
        vm.showDataEvent.observe(this, Observer {
            navigateTo(TNCertDataActivity::class.java){
                putExtra(Extras.EXTRA_CERT_ORDER_ID,CertOperations.getTNCertOrderId())
            }
        })
        binding.vm = vm
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            2333-> {
                if (resultCode == 666) {
                     finish()
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }


    private val expiredFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    fun expiredText(expired: Long?): String {
        if (expired == null || expired < 0) {
            return ""
        }
        return expiredFormat.format(expired)
    }

    override fun onResume() {
        super.onResume()
        Networkutils.chainGateway.getCertData(Networkutils.passportRepository.currPassport.value!!.address, ChainGateway.TN_COMMUNICATION_LOG)
            .checkonMain()
            .subscribe({
                it.content.let {
                    it.let {
                        saveTnLogCertExpired(it!!.expired)
                        vm.value3.set(expiredText(it!!.expired))

                        if (0L != it!!.expired) {
                        }
                    }
                }
            }, {})
    }
}
