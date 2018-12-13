package io.wexchain.android.dcc.modules.cashloan.act

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.*
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.domain.CertificationType
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.modules.cashloan.bean.CertificationInfo
import io.wexchain.android.dcc.tools.toBean
import io.wexchain.android.dcc.tools.toJson
import io.wexchain.android.dcc.vm.AuthenticationStatusVm
import io.wexchain.android.dcc.vm.domain.UserCertStatus
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityCashCertificationBinding
import io.wexchain.dccchainservice.CertApi
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.CashLoanRequest
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import io.wexchain.ipfs.utils.io_main
import worhavah.certs.bean.TNcert1newreport
import worhavah.regloginlib.tools.check
import worhavah.tongniucertmodule.SubmitTNLogActivity
import worhavah.tongniucertmodule.TnLogCertificationActivity
import java.util.concurrent.TimeUnit

/**
 *Created by liuyang on 2018/10/15.
 */
class CashCertificationActivity : BindActivity<ActivityCashCertificationBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_cash_certification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        initVm()
        refreshCertStatus()
        initClick()
    }

    var aa = 7

    private fun refreshCertStatus() {
        binding.asTongniuVm?.let {
            if (it.status.get() == UserCertStatus.INCOMPLETE) {
                val passport = App.get().passportRepository.getCurrentPassport()!!
                getTNLogReport(passport)
                        .zipWith(Single.timer(1, TimeUnit.SECONDS))
                        .flatMap {
                            if (null != it) {
                                if (it.first.endorserOrder.status.contains("COMPELETED")) {
                                    worhavah.certs.tools.CertOperations.onTNLogSuccessGot(it.first.tongniuData.toString())
                                    worhavah.certs.tools.CertOperations.certed()
                                    worhavah.certs.tools.CertOperations.clearTNCertCache()
                                    initVm()
                                } else if (it.first.endorserOrder.status.contains("INVALID")) {
                                    toast("获取报告失败")
                                    worhavah.certs.tools.CertOperations.certPrefs.certTNLogState.set(
                                            worhavah.certs.tools.UserCertStatus.NONE.name)
                                    worhavah.certs.tools.CertOperations.clearTNCertCache()
                                    aa = 0
                                    initVm()
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
                        .io_main()
                        .subscribeBy(
                                onSuccess = {
                                    val content = it.content
                                    if (0L != content.expired) {
                                        worhavah.certs.tools.CertOperations.saveTnLogCertExpired(content.expired)
                                    }
                                },
                                onError = {
                                    toast("获取报告失败")
                                    worhavah.certs.tools.CertOperations.certPrefs.certTNLogState.set(
                                            worhavah.certs.tools.UserCertStatus.NONE.name)
                                    worhavah.certs.tools.CertOperations.clearTNCertCache()
                                    aa = 0
                                    initVm()
                                })
            }
        }
    }

    private fun getTNLogReport(passport: Passport): Single<TNcert1newreport> {
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
                        "orderId" to orderId.toString()))
        ).compose(Result.checked())
    }

    private fun initClick() {
        binding.cert!!.apply {
            userinfoCall.observe(this@CashCertificationActivity, Observer {
                navigateTo(UserInfoCertificationActivity::class.java)
            })
            tipsCall.observe(this@CashCertificationActivity, Observer {
                toast("DCC不足?")
            })
            commitCall.observe(this@CashCertificationActivity, Observer {
                doApply()
            })
        }
    }

    private fun doApply() {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.createLoanOrder(it)
                }
                .map {
                    val idData = CertOperations.getCertIdData()!!
                    val certIdPics = CertOperations.getCertIdPics()!!
                    val bankData = CertOperations.getCertBankCardData()!!
                    val tnCmData = worhavah.certs.tools.CertOperations.getTnLogPhoneNo()!!
                    val tnCmLog = worhavah.certs.tools.CertOperations.certPrefs.certTNLogData.get()!!
                    val infoCert = App.get().passportRepository.getUserInfoCert()!!
                    val certInfo = infoCert.toBean(CertificationInfo::class.java)

                    val list = mutableListOf<CashLoanRequest.ExtraPersonalInfo.ContactInfo>()
                    val contact1 = CashLoanRequest.ExtraPersonalInfo.ContactInfo(certInfo.Contacts1Relation!!, certInfo.Contacts1Name!!, certInfo.Contacts1Phone!!)
                    val contact2 = CashLoanRequest.ExtraPersonalInfo.ContactInfo(certInfo.Contacts2Relation!!, certInfo.Contacts2Name!!, certInfo.Contacts2Phone!!)
                    val contact3 = CashLoanRequest.ExtraPersonalInfo.ContactInfo(certInfo.Contacts3Relation!!, certInfo.Contacts3Name!!, certInfo.Contacts3Phone!!)
                    list.add(contact1)
                    list.add(contact2)
                    list.add(contact3)

                    val index = CashLoanRequest.Index(it.orderId)
                    val idInfo = CashLoanRequest.IdCertInfo(idData.name, idData.id)
                    val bankInfo = CashLoanRequest.BankCardCertInfo(bankData.bankCardNo, bankData.phoneNo)
                    val tnCmInfo = CashLoanRequest.CommunicationLogCertInfo(tnCmData, tnCmLog)
                    val extraInfo = CashLoanRequest.ExtraPersonalInfo(
                            maritalStatus = certInfo.MarriageStatus,
                            residentialProvince = certInfo.ResideProvince!!,
                            residentialCity = certInfo.ResideCity!!,
                            residentialDistrict = certInfo.ResideArea!!,
                            residentialAddress = certInfo.ResideAddress!!,
                            loanUsage = certInfo.LoanPurpose!!,
                            workingType = certInfo.WorkCategory!!,
                            workingIndustry = certInfo.WorkIndustry!!,
                            workingYears = certInfo.WorkYear!!,
                            companyProvince = certInfo.CompanyProvince!!,
                            companyCity = certInfo.CompanyCity!!,
                            companyDistrict = certInfo.CompanyArea!!,
                            companyAddress = certInfo.CompanyAddress!!,
                            companyName = certInfo.CompanyName!!,
                            companyTel = certInfo.CompanyPhone,
                            contactInfoList = list
                    )
                    val request = CashLoanRequest(
                            index = index,
                            idCertInfo = idInfo,
                            bankCardCertInfo = bankInfo,
                            communicationLogCertInfo = tnCmInfo,
                            extraPersonalInfo = extraInfo)

                    certIdPics to request
                }
                .flatMap {
                    val pic = it.first
                    val data = it.second
                    ScfOperations.withScfTokenInCurrentPassport {
                        App.get().scfApi.tnApply(
                                token = it,
                                data = data.toJson(),
                                idCardFrontPic = CertApi.uploadFilePart(pic.first, "front.jpg", "image/jpeg", "idCardFrontPic"),
                                idCardBackPic = CertApi.uploadFilePart(pic.second, "back.jpg", "image/jpeg", "idCardBackPic"),
                                facePic = CertApi.uploadFilePart(pic.third, "user.jpg", "image/jpeg", "facePic")
                        )
                    }
                }
                .io_main()
                .doOnError {
                    if (it is DccChainServiceException) {
                        toast(it.message ?: "系统错误")
                    } else {
                        toast("系统错误")
                    }
                }
                .withLoading()
                .subscribeBy {
                    finish()
                }
    }

    private fun initVm() {
        binding.asIdVm = obtainAuthStatus(CertificationType.ID)
        binding.asBankVm = obtainAuthStatus(CertificationType.BANK)
        binding.asTongniuVm = obtainAuthStatus(CertificationType.TONGNIU)
        binding.cert = getViewModel()
        binding.vm = getViewModel()
    }

    private fun obtainAuthStatus(certificationType: CertificationType): AuthenticationStatusVm? {
        return ViewModelProviders.of(this)[certificationType.name, AuthenticationStatusVm::class.java]
                .apply {
                    title.set(getCertTypeTitle(certificationType))
                    authDetail.set(getDescription(certificationType))
                    this.status.set(CertOperations.getCertStatus(certificationType))
                    this.certificationType.set(certificationType)
                    this.performOperationEvent.observe(this@CashCertificationActivity, Observer {
                        it?.let {
                            val type = it.first
                            val certStatus = it.second
                            if (type != null && certStatus != null) {
                                this@CashCertificationActivity.performOperation(type, certStatus)
                            }
                        }
                    })
                }
    }


    private fun getCertTypeTitle(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.id_verification)
            CertificationType.BANK -> getString(R.string.bank_account_verification)
            CertificationType.TONGNIU -> "同牛运营商认证"
            else -> ""
        }
    }

    private fun getDescription(certificationType: CertificationType): String {
        return when (certificationType) {
            CertificationType.ID -> getString(R.string.verify_your_legal_documentation)
            CertificationType.BANK -> getString(R.string.for_quick_approvalto_improve)
            CertificationType.TONGNIU -> "用于现金借贷审核"
            else -> ""
        }
    }

    private fun performOperation(certificationType: CertificationType, status: UserCertStatus) {
        when (certificationType) {
            CertificationType.ID -> {
                if (status == UserCertStatus.DONE) {
                    navigateTo(IdCertificationActivity::class.java) {
                        putExtra("type", CERT_TYPE_CASHLOAN)
                    }
                } else {
                    navigateTo(SubmitIdActivity::class.java) {
                        putExtra("type", CERT_TYPE_CASHLOAN)
                    }
                }
            }

            CertificationType.BANK ->
                if (status == UserCertStatus.DONE) {
                    navigateTo(BankCardCertificationActivity::class.java) {
                        putExtra("type", CERT_TYPE_CASHLOAN)
                    }
                } else {
                    PassportOperations.ensureCaValidity(this) {
                        navigateTo(SubmitBankCardActivity::class.java) {
                            putExtra("type", CERT_TYPE_CASHLOAN)
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
                            startActivity(Intent(this, SubmitTNLogActivity::class.java))
                        }
                    }
                    UserCertStatus.INCOMPLETE -> {

                    }
                    else -> {

                    }
                }

            }
            else -> {
            }
        }
    }

    companion object {
        const val CERT_TYPE_CASHLOAN = "cert_type_cashloan"
    }


}