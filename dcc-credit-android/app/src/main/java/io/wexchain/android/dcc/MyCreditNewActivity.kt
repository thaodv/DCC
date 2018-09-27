package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.Pop
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.chain.IpfsOperations.checkKey
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.modules.ipfs.activity.MyCloudActivity
import io.wexchain.android.dcc.modules.ipfs.activity.OpenCloudActivity
import io.wexchain.android.dcc.view.CountDownProgress
import worhavah.regloginlib.tools.check
import io.wexchain.android.dcc.view.dialog.DeleteAddressBookDialog
import io.wexchain.android.dcc.vm.AuthenticationStatusVm
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyNewcreditBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.ipfs.utils.io_main
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.DynamicBytes
import worhavah.certs.bean.TNcertReport
import worhavah.certs.tools.CertOperations.onTNLogSuccessGot
import worhavah.certs.tools.CertOperations.saveTnLogCertExpired
import worhavah.tongniucertmodule.SubmitTNLogActivity
import worhavah.tongniucertmodule.TnLogCertificationActivity
import java.math.BigDecimal
import java.util.*


class MyCreditNewActivity : BindActivity<ActivityMyNewcreditBinding>() {

    override val contentLayoutId: Int = R.layout.activity_my_newcredit

    private val passport by lazy {
        App.get().passportRepository
    }
    private lateinit var countDownProgress: CountDownProgress
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setWindowExtended()
        initToolbarS()
        countDownProgress = findViewById<View>(R.id.countdownProgress) as CountDownProgress
        countDownProgress.setCountdownTime(900)
        setVM()
    }
    val totalCerts  =4
    var certDoneNum=0
    var lastper=-1
    fun setVM(){
        certDoneNum=0
        binding.asIdVm = obtainAuthStatus(CertificationType.ID)
        binding.asBankVm = obtainAuthStatus(CertificationType.BANK)
        binding.asMobileVm = obtainAuthStatus(CertificationType.MOBILE)
        binding.asPersonalVm = obtainAuthStatus(CertificationType.PERSONAL)
        binding.asTongniuVm = obtainAuthStatus(CertificationType.TONGNIU)
        binding.asLoanVm = obtainAuthStatus(CertificationType.LOANREPORT)

        var per=BigDecimal(certDoneNum*100/totalCerts).toBigInteger().toInt()
        if(per!=lastper){
            lastper=per
            countDownProgress.setPercent(per)
            countDownProgress.startCountDownTime(  CountDownProgress.OnCountdownFinishListener  {
            })
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
                                putExtra("activity_type", PassportSettingsActivity.NOT_OPEN_CLOUD)
                            }
                        } else {
                            if (ipfsKeyHash.isNullOrEmpty()) {
                                navigateTo(OpenCloudActivity::class.java) {
                                    putExtra("activity_type", PassportSettingsActivity.OPEN_CLOUD)
                                }
                            } else {
                                if (ipfsKeyHash == it) {
                                    navigateTo(MyCloudActivity::class.java)
                                } else {
                                    passport.setIpfsKeyHash("")
                                    navigateTo(OpenCloudActivity::class.java) {
                                        putExtra("activity_type", PassportSettingsActivity.OPEN_CLOUD)
                                    }
                                }
                            }
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
                    if(CertOperations.getCertStatus(certificationType)==UserCertStatus.DONE){
                        certDoneNum+=1
                    }
                    this.certificationType.set(certificationType)
                    this.performOperationEvent.observe(this@MyCreditNewActivity, Observer {
                        it?.let {
                            val type = it.first
                            val certStatus = it.second
                            if (type != null && certStatus != null) {
                                this@MyCreditNewActivity.performOperation(type, certStatus)
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
    }

     var aa=15;
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
                                    }
                                },
                                onError = {
                                    Pop.toast(it.message ?: "系统错误", this)
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

                        if(null!=it){
                            //Log.e("getTNLogReport",it.reportData.toString())
                            if(it.isComplete.equals("Y")){
                                android.util.Log.e("it.reportData ", it.reportData.toString() )
                                onTNLogSuccessGot(it.reportData.toString() )
                                setVM()
                               // getTNrealdata()
                            }
                        }
                        App.get().chainGateway.getCertData(passport.address, ChainGateway.TN_COMMUNICATION_LOG).check()
                    }
                    .doFinally {
                    aa+=-1
                    if(aa>0){
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
                            Pop.toast(it.message ?: "系统错误", this)
                        })

            }
        }
    }
    fun getTNrealdata(){
        getTNLogReport2(App.get().passportRepository.getCurrentPassport()!!) .subscribeBy(
            onSuccess = {
                android.util.Log.e(" getTNLogReport2 ", it  )
                val ss=it
                var dd=ss.substring(it.indexOf("\"reportData\":\"")+14,it.length-2)
                android.util.Log.e(" getTNLogReport2 dddddd", dd  )
                onTNLogSuccessGot(dd )
                setVM()

                it
            }
        )
    }

    fun getTNLogReport(passport: Passport): Single<TNcertReport> {
        require(passport.authKey != null)
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        val orderId =worhavah.certs.tools.CertOperations.certPrefs.certTNLogOrderId.get()

           return  worhavah.certs.tools.CertOperations.tnCertApi.TNgetReport(
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
    fun getTNLogReport2(passport: Passport): Single<String> {
        require(passport.authKey != null)
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        val orderId =worhavah.certs.tools.CertOperations.certPrefs.certTNLogOrderId.get()

        return  worhavah.certs.tools.CertOperations.tnCertApi.TNgetReport2(
            address = address,
            orderId = orderId,

            signature = ParamSignatureUtil.sign(
                privateKey, mapOf(
                    "address" to address,
                    "orderId" to orderId.toString()
                )
            )
        )
        //.compose(Result.checked())
    }

    private fun getDescription(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.verify_your_legal_documentation)
            CertificationType.PERSONAL -> getString(R.string.safer_assessment)
            CertificationType.BANK -> getString(R.string.for_quick_approvalto_improve)
            CertificationType.MOBILE -> getString(R.string.to_improve_the_approval)
            CertificationType.TONGNIU -> getString(R.string.to_improve_the_approval)
            CertificationType.LOANREPORT -> "借贷记录全整合"
        }
    }

    private fun getCertTypeTitle(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.id_verification)
            CertificationType.PERSONAL -> getString(R.string.verify_your_legal_documentation)
            CertificationType.BANK -> getString(R.string.bank_account_verification)
            CertificationType.MOBILE -> getString(R.string.carrier_verification)
            CertificationType.TONGNIU -> "同牛认证"
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
        val dialog = DeleteAddressBookDialog(this@MyCreditNewActivity)
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
