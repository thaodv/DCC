package io.wexchain.android.dcc.modules.cert

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.chain.IpfsOperations.checkKey
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.modules.ipfs.activity.MyCloudActivity
import io.wexchain.android.dcc.modules.ipfs.activity.OpenCloudActivity
import io.wexchain.android.dcc.modules.loan.LoanReportActivity
import io.wexchain.android.dcc.modules.mine.SettingActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.CountDownProgress
import io.wexchain.android.dcc.view.dialog.DeleteAddressBookDialog
import io.wexchain.android.dcc.vm.AuthenticationStatusVm
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyCreditBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.ipfs.utils.io_main
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.DynamicBytes
import worhavah.certs.bean.TNcert1newreport
import worhavah.certs.tools.CertOperations.clearTNCertCache
import worhavah.certs.tools.CertOperations.onTNLogSuccessGot
import worhavah.certs.tools.CertOperations.saveTnLogCertExpired
import worhavah.regloginlib.Net.Networkutils
import worhavah.tongniucertmodule.SubmitTNLogActivity
import worhavah.tongniucertmodule.TnLogCertificationActivity
import java.util.*


class MyCreditActivity : BindActivity<ActivityMyCreditBinding>() {

    override val contentLayoutId: Int = R.layout.activity_my_credit

    private val passport by lazy {
        App.get().passportRepository
    }
    private lateinit var countDownProgress: CountDownProgress
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        countDownProgress = findViewById<View>(R.id.countdownProgress) as CountDownProgress
        countDownProgress.setCountdownTime(900)
        setVM()

        val passport = Networkutils.passportRepository.getCurrentPassport()
        if (passport == null) {
            Networkutils.passportRepository.load()
        }
    }

    val totalCerts = 3
    var certDoneNum = 0
    var lastper = -1
    fun setVM() {
        certDoneNum = 0
        binding.asIdVm = obtainAuthStatus(CertificationType.ID)
        binding.asBankVm = obtainAuthStatus(CertificationType.BANK)
        binding.asMobileVm = obtainAuthStatus(CertificationType.MOBILE)
        binding.asPersonalVm = obtainAuthStatus(CertificationType.PERSONAL)
        binding.asTongniuVm = obtainAuthStatus(CertificationType.TONGNIU)
        binding.asLoanVm = obtainAuthStatus(CertificationType.LOANREPORT)

        //var per = BigDecimal(certDoneNum * 100 / totalCerts).toBigInteger().toInt()

        var per: Int

        if (1 == certDoneNum) {
            per = 33
        } else if (2 == certDoneNum) {
            per = 67
        } else if (3 == certDoneNum) {
            per = 100
        } else {
            per = 0
        }

        runOnMainThread {
            if (per != lastper) {
                lastper = per
                countDownProgress.setPercent(per)
                countDownProgress.startCountDownTime(CountDownProgress.OnCountdownFinishListener {
                })
            }
        }


    }

    private fun getCloudToken() {
        IpfsOperations.getIpfsKey()
                .checkKey()
                .io_main()
                .subscribeBy {
                    val ipfsKeyHash = passport.getIpfsKeyHash()
                    binding.creditIpfsCloud.onClick {
                        if (it.isEmpty()) {
                            navigateTo(OpenCloudActivity::class.java) {
                                putExtra("activity_type", SettingActivity.NOT_OPEN_CLOUD)
                            }
                        } else {
                            if (ipfsKeyHash.isNullOrEmpty()) {
                                navigateTo(OpenCloudActivity::class.java) {
                                    putExtra("activity_type", SettingActivity.OPEN_CLOUD)
                                }
                            } else {
                                if (ipfsKeyHash == it) {
                                    navigateTo(MyCloudActivity::class.java)
                                } else {
                                    passport.setIpfsKeyHash("")
                                    navigateTo(OpenCloudActivity::class.java) {
                                        putExtra("activity_type", SettingActivity.OPEN_CLOUD)
                                    }
                                }
                            }
                        }
                    }
                }
    }

    private fun obtainAuthStatus(certificationType: CertificationType): AuthenticationStatusVm? {
        return ViewModelProviders.of(this)[certificationType.name, AuthenticationStatusVm::class.java]
                .apply {
                    //todo
                    title.set(getCertTypeTitle(certificationType))
                    authDetail.set(getDescription(certificationType))
                    this.status.set(CertOperations.getCertStatus(certificationType))
                    if (CertOperations.getCertStatus(certificationType) == UserCertStatus.DONE) {
                        certDoneNum += 1
                    }
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
        setVM()
        refreshCertStatus()
        getCloudToken()
        worhavah.certs.tools.CertOperations.certPrefs.certLasttime.get().apply {
            if (TextUtils.isEmpty(this)) {
                binding.tvShowtime.visibility = View.GONE
            } else {
                binding.tvShowtime.visibility = View.VISIBLE
                binding.tvShowtime.text = "更新时间：" + this
            }
        }
    }

    var aa = 15;
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
                        .flatMap {
                            val data = it
                            if (data.fail) {
                                // generate report fail
                                CertOperations.onCmLogFail()
//                                refreshCertStatus()
                            } else {
                                val reportData = data.reportData
                                if (data.hasCompleted() && reportData != null) {
                                    CertOperations.onCmLogSuccessGot(reportData)
                                }
                            }
                            App.get().chainGateway.getCertData(passport.address, ChainGateway.BUSINESS_COMMUNICATION_LOG).check()
                        }
                        .io_main()
                        .doFinally {
                            refreshCertStatus()
                        }
                        .subscribeBy(
                                onSuccess = {
                                    val content = it.content
                                    if (0L != content.expired) {
                                        CertOperations.saveCmLogCertExpired(content.expired)
                                        worhavah.certs.tools.CertOperations.certed()
                                    }
                                },
                                onError = {
                                    toast(it.message ?: getString(R.string.system_error))
                                })
            }
        }

        binding.asTongniuVm?.let {
            if (it.status.get() == UserCertStatus.INCOMPLETE) {
                //     if (true) {
                //get report
                val passport = App.get().passportRepository.getCurrentPassport()!!
                getTNLogReport(passport)
                        .flatMap {

                            if (null != it) {
                                //Log.e("getTNLogReport",it.reportData.toString())
                                if (it.endorserOrder.status.contains("COMPELETED")) {
                                    //  android.util.Log.e("it.reportData ", it.tongniuData.toString() )
                                    onTNLogSuccessGot(it.tongniuData.toString())
                                    worhavah.certs.tools.CertOperations.certed()
                                    clearTNCertCache()
                                    setVM()
                                    // getTNrealdata()
                                } else if (it.endorserOrder.status.contains("INVALID")) {
                                    toast("获取报告失败")
                                    worhavah.certs.tools.CertOperations.certPrefs.certTNLogState.set(
                                            worhavah.certs.tools.UserCertStatus.NONE.name)
                                    clearTNCertCache()
                                    aa = 0
                                    setVM()
                                }
                            }
                            App.get().chainGateway.getCertData(passport.address, ChainGateway.TN_COMMUNICATION_LOG).check()
                        }
                        .doFinally {
                            aa += -1
                            if (aa > 0) {
                                refreshCertStatus()
                            }
                        }
                        .subscribeBy(
                                onSuccess = {
                                    val content = it.content
                                    if (0L != content.expired) {
                                        saveTnLogCertExpired(content.expired)
                                    }
                                    /* if(null!=it){
                                         if(it.isComplete.equals("Y")){
                                             onTNLogSuccessGot(it.reportData.toString())
                                             setVM()
                                         }
                                     }*/
                                },
                                onError = {
                                    toast("获取报告失败")
                                    worhavah.certs.tools.CertOperations.certPrefs.certTNLogState.set(
                                            worhavah.certs.tools.UserCertStatus.NONE.name)
                                    clearTNCertCache()
                                    aa = 0
                                    setVM()
                                    // Pop.toast(it.message ?: "系统错误", this)
                                })

            }
        }
    }

    fun getTNLogReport(passport: Passport): Single<TNcert1newreport> {
        require(passport.authKey != null)
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        val orderId = worhavah.certs.tools.CertOperations.certPrefs.certTNLogOrderId.get()

        return worhavah.certs.tools.CertOperations.tnCertApi.TNgetReport(
                address = address,
                orderId = orderId,

                signature = ParamSignatureUtil.sign(
                        privateKey, mapOf(
                        "address" to address,
                        "orderId" to orderId.toString()
                )
                )
        ).compose(Result.checked())
        //.compose(Result.checked())
    }

    private fun getDescription(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.verify_your_legal_documentation)
            CertificationType.PERSONAL -> getString(R.string.safer_assessment)
            CertificationType.BANK -> getString(R.string.for_quick_approvalto_improve)
            CertificationType.MOBILE -> getString(R.string.to_improve_the_approval)
            CertificationType.TONGNIU -> "用于现金借贷审核"
            CertificationType.LOANREPORT -> "借贷记录全整合"
        }
    }

    private fun getCertTypeTitle(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.id_verification)
            CertificationType.PERSONAL -> getString(R.string.verify_your_legal_documentation)
            CertificationType.BANK -> getString(R.string.bank_account_verification)
            CertificationType.MOBILE -> getString(R.string.carrier_verification)
            CertificationType.TONGNIU -> "同牛运营商认证"
            CertificationType.LOANREPORT -> "借贷报告"
        }
    }

    fun checkIpfsAndChainDigest(business: String): Single<Boolean> {
        return IpfsOperations.getIpfsToken(business)
                .map {
                    if (it.result.toString() == "0x") {
                        arrayListOf()
                    } else {
                        FunctionReturnDecoder.decode(it.result, Erc20Helper.decodeTokenResponse())
                    }
                }
                .map {
                    val digest1: ByteArray
                    val digest2: ByteArray
                    if (it.size == 0) {
                        digest1 = byteArrayOf()
                        digest2 = byteArrayOf()
                    } else {
                        digest1 = (it[6] as DynamicBytes).value
                        digest2 = (it[7] as DynamicBytes).value
                    }
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
                                .io_main()
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
            CertificationType.TONGNIU -> {
                when (status) {
                    UserCertStatus.DONE, UserCertStatus.TIMEOUT -> {
                        // navigateTo(TnLogCertificationActivity::class.java)
                        startActivity(Intent(this, TnLogCertificationActivity::class.java))
                    }
                    UserCertStatus.NONE -> {
                        PassportOperations.ensureCaValidity(this) {
                            //  navigateTo(worhavah.tongniucertmodule.TNCertDataActivity::class.java)
                            //     startActivity(Intent(this, TnLogCertificationActivity::class.java))
                            startActivity(Intent(this, SubmitTNLogActivity::class.java))
                        }
                    }
                    UserCertStatus.INCOMPLETE -> {
                        // get report processing
                        //      startActivity(Intent(this, SubmitTNLogActivity::class.java))
                    }
                }

            }
            CertificationType.LOANREPORT -> {
                PassportOperations.ensureCaValidity(this) {
                    //  navigateTo(worhavah.tongniucertmodule.TNCertDataActivity::class.java)
                    //     startActivity(Intent(this, TnLogCertificationActivity::class.java))
                    //    startActivity(Intent(this, SubmitTNLogActivity::class.java))
                    navigateTo(LoanReportActivity::class.java)
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
                        putExtra("activity_type", SettingActivity.OPEN_CLOUD)
                    }
                } else {
                    navigateTo(MyCloudActivity::class.java)
                }
            }
        })
        dialog.show()
    }


}
