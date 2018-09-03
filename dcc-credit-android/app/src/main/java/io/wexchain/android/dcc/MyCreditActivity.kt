package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.Toolbar
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.setWindowExtended
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.chain.IpfsOperations.checkToken
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.modules.ipfs.activity.MyCloudActivity
import io.wexchain.android.dcc.modules.ipfs.activity.OpenCloudActivity
import io.wexchain.android.dcc.view.dialog.DeleteAddressBookDialog
import io.wexchain.android.dcc.vm.AuthenticationStatusVm
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyCreditBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.ipfs.utils.doMain
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.DynamicBytes
import java.util.*

class MyCreditActivity : BindActivity<ActivityMyCreditBinding>() {

    override val contentLayoutId: Int = R.layout.activity_my_credit

    private val passport by lazy {
        App.get().passportRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowExtended()
        initToolbarS()
        binding.asIdVm = obtainAuthStatus(CertificationType.ID)
        binding.asBankVm = obtainAuthStatus(CertificationType.BANK)
        binding.asMobileVm = obtainAuthStatus(CertificationType.MOBILE)
        binding.asPersonalVm = obtainAuthStatus(CertificationType.PERSONAL)
    }

    private fun initIpfsCloud() {
        val ipfsKeyHash = passport.getIpfsKeyHash()
        if (ipfsKeyHash.isNullOrEmpty()) {
            getCloudToken()
        } else {
            binding.creditIpfsCloud.onClick {
                navigateTo(MyCloudActivity::class.java)
            }
        }
    }

    private fun getCloudToken() {
        IpfsOperations.getIpfsKey()
                .checkToken()
                .doMain()
                .subscribeBy {
                    binding.creditIpfsCloud.onClick {
                        navigateTo(OpenCloudActivity::class.java) {
                            putExtra("activity_type", if (it) PassportSettingsActivity.OPEN_CLOUD else PassportSettingsActivity.NOT_OPEN_CLOUD)
                        }
                    }
                }
    }

    private fun initToolbarS(showHomeAsUp: Boolean = true): Toolbar? {
        toolbar = findViewById(R.id.toolbar)
        val tb = toolbar
        if (tb != null) {
            setSupportActionBar(toolbar)
            toolbarTitle = findViewById(R.id.toolbar_title)
            intendedTitle?.let { title = it }
        }
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(showHomeAsUp)
            setDisplayShowHomeEnabled(showHomeAsUp)
            if (toolbarTitle != null) {
                setDisplayShowTitleEnabled(false)
            }
        }
        return toolbar
    }

    private fun obtainAuthStatus(certificationType: CertificationType): AuthenticationStatusVm? {
        return ViewModelProviders.of(this)[certificationType.name, AuthenticationStatusVm::class.java]
                .apply {
                    //todo
                    title.set(getCertTypeTitle(certificationType))
                    authDetail.set(getDescription(certificationType))
                    this.status.set(CertOperations.getCertStatus(certificationType))
                    this.certificationType.set(certificationType)
                    this.performOperationEvent.observe(this@MyCreditActivity, Observer {
                        it?.let {
                            val type = it.first
                            val certStatus = it.second
                            if (type != null && certStatus != null) {
                                this@MyCreditActivity.performOperation(type, certStatus)
                            }
                        }
                    })
                }
    }

    override fun onResume() {
        super.onResume()
        refreshCertStatus()
        initIpfsCloud()
    }

    private fun refreshCertStatus() {
        listOf(binding.asIdVm, binding.asBankVm, binding.asMobileVm, binding.asPersonalVm).forEach {
            it?.let { vm ->
                vm.certificationType.get()?.let {
                    vm.status.set(CertOperations.getCertStatus(it))
                }
            }
        }
        binding.asMobileVm?.let {
            if (it.status.get() == UserCertStatus.INCOMPLETE) {
                //get report
                val passport = App.get().passportRepository.getCurrentPassport()!!
                CertOperations.getCommunicationLogReport(passport)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it.fail) {
                                // generate report fail
                                CertOperations.onCmLogFail()
                                refreshCertStatus()
                            } else {
                                val reportData = it.reportData
                                if (it.hasCompleted() && reportData != null) {
                                    CertOperations.onCmLogSuccessGot(reportData)
                                    refreshCertStatus()
                                }
                            }
                        }, {
                            Pop.toast(it.message ?: "系统错误", this)
                        })
            }
        }
    }

    private fun getDescription(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.verify_your_legal_documentation)
            CertificationType.PERSONAL -> getString(R.string.safer_assessment)
            CertificationType.BANK -> getString(R.string.for_quick_approvalto_improve)
            CertificationType.MOBILE -> getString(R.string.to_improve_the_approval)
        }
    }

    private fun getCertTypeTitle(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.id_verification)
            CertificationType.PERSONAL -> getString(R.string.verify_your_legal_documentation)
            CertificationType.BANK -> getString(R.string.bank_account_verification)
            CertificationType.MOBILE -> getString(R.string.carrier_verification)
        }
    }

    fun checkIpfsAndChainDigest(business: String): Single<Boolean> {
        return IpfsOperations.getIpfsToken(business)
                .map {
                    FunctionReturnDecoder.decode(it.result, Erc20Helper.dncodeResponse())
                }
                .map {
                    val digest1 = (it[6] as DynamicBytes).value
                    val digest2 = (it[7] as DynamicBytes).value
                    Pair(digest1, digest2)
                }
                .map {
                    val chainDigest = CertOperations.getChainDigest(business).blockingGet()
                    if (chainDigest.first.isEmpty() && it.first.isEmpty()) {
                        false
                    } else {
                        (Arrays.equals(chainDigest.first, it.first) && Arrays.equals(chainDigest.second, it.second))
                    }
                }
    }

    private fun performOperation(certificationType: CertificationType, status: UserCertStatus) {
        when (certificationType) {
            CertificationType.ID -> {
                if (status == UserCertStatus.DONE) {
                    navigateTo(IdCertificationActivity::class.java)
                } else {
                    PassportOperations.ensureCaValidity(this) {
                        checkIpfsAndChainDigest(ChainGateway.BUSINESS_ID)
                                .subscribeOn(Schedulers.io())
                                .doMain()
                                .withLoading()
                                .filter {
                                    if (!it) navigateTo(SubmitIdActivity::class.java)
                                    it
                                }
                                .subscribeBy(
                                        onSuccess = {
                                            showIpfsDialog()
                                        },
                                        onError = {
                                            navigateTo(SubmitIdActivity::class.java)
                                        })
                    }
                }
            }
            CertificationType.PERSONAL -> {
//                navigateTo(SubmitPersonalInfoActivity::class.java)
            }
            CertificationType.BANK ->
                if (status == UserCertStatus.DONE) {
                    navigateTo(BankCardCertificationActivity::class.java)
                } else {
                    PassportOperations.ensureCaValidity(this) {
                        navigateTo(SubmitBankCardActivity::class.java)
                    }
                }
            CertificationType.MOBILE -> {
                when (status) {
                    UserCertStatus.DONE, UserCertStatus.TIMEOUT -> {
                        navigateTo(CmLogCertificationActivity::class.java)
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

    private fun showIpfsDialog() {
        val dialog = DeleteAddressBookDialog(this@MyCreditActivity)
        dialog.setTvText("您已在云端备份过实名认证数据，下载到本地后无需再次认证。")
        dialog.setBtnText("去下载", "取消")
        dialog.setOnClickListener(object : DeleteAddressBookDialog.OnClickListener {
            override fun cancel() {
                dialog.dismiss()
                navigateTo(SubmitIdActivity::class.java)
            }

            override fun sure() {
                dialog.dismiss()
                val ipfsKeyHash = passport.getIpfsKeyHash()
                if (ipfsKeyHash.isNullOrEmpty()) {
                    navigateTo(OpenCloudActivity::class.java) {
                        putExtra("activity_type", PassportSettingsActivity.OPEN_CLOUD)
                    }
                } else {
                    navigateTo(MyCloudActivity::class.java)
                }
            }
        })
        dialog.show()
    }


}
