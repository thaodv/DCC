package io.wexchain.android.dcc

import android.os.Bundle
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.replaceFragment
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.fragment.InputPhoneInfoFragment
import io.wexchain.android.dcc.fragment.VerifyCarrierQueryPasswordFragment
import io.wexchain.android.dcc.fragment.VerifyCarrierSmsCodeFragment
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.CertProcess

class SubmitCommunicationLogActivity : BaseCompatActivity(), InputPhoneInfoFragment.Listener, VerifyCarrierSmsCodeFragment.Listener, VerifyCarrierQueryPasswordFragment.Listener {

    private val inputPhoneInfoFragment by lazy { InputPhoneInfoFragment.create(this) }
    private val verifyCarrierSmsCodeFragment by lazy { VerifyCarrierSmsCodeFragment.create(this) }
    private val verifyCarrierQueryPasswordFragment by lazy { VerifyCarrierQueryPasswordFragment.create(this) }

    private lateinit var passport: Passport
    private lateinit var realName: String
    private lateinit var realId: String

    private var submitOrderId: Long? = null
    private var submitPhoneNo: String? = null
    private var submitServicePassword: String? = null

    private var currentProcess: CertProcess? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_fragment_container)
        initToolbar()
        checkPreconditions()
    }

    override fun goBack(): Boolean {
        val curr = supportFragmentManager.findFragmentById(R.id.fl_container)
        return if(curr!=inputPhoneInfoFragment){
            replaceFragment(inputPhoneInfoFragment, R.id.fl_container)
            true
        }else{
            super.goBack()
        }
    }

    override fun onSubmitPhoneInfo(phoneNo: String, password: String) {
        this.submitPhoneNo = phoneNo
        this.submitServicePassword = password
        CertOperations.confirmCertFee({ supportFragmentManager }, ChainGateway.BUSINESS_COMMUNICATION_LOG) {
            val passport = passport
            obtainOrderId(passport)
                    .flatMap {
                        CertOperations.requestCommunicationLog(passport, it, realName, realId, phoneNo, password)
                    }
                    .doOnSubscribe {
                        showLoadingDialog()
                    }
                    .doFinally {
                        hideLoadingDialog()
                    }
                    .subscribe { process ->
                        handleCertProcess(process)
                    }
        }
    }

    override fun onSubmitSmsCode(code: String) {
        val process = currentProcess
        if(process != null){
            when(process.wrapCode()){
                CertProcess.Code.CODE_10001,
                CertProcess.Code.CODE_10002 -> processWithSmsCode(process,code)
                else -> {
                    //illegal state
                }
            }
        }
    }

    override fun onSubmitQueryPwd(code: String) {
        val process = currentProcess
        if(process != null && process.wrapCode() == CertProcess.Code.CODE_10022){
            processWithQueryPwd(process,code)
        }
    }

    private fun processWithQueryPwd(process: CertProcess, code: String) {
        val passport = passport
        val phoneNo = submitPhoneNo!!
        val password = submitServicePassword!!
        obtainOrderId(passport)
                .flatMap {
                    CertOperations.advanceCommunicationLog(passport, it, realName, realId, phoneNo, password,process,queryPwd = code)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe { it ->
                    handleCertProcess(it)
                }
    }

    private fun processWithSmsCode(process: CertProcess, code: String) {
        val passport = passport
        val phoneNo = submitPhoneNo!!
        val password = submitServicePassword!!
        obtainOrderId(passport)
                .flatMap {
                    CertOperations.advanceCommunicationLog(passport, it, realName, realId, phoneNo, password,process,captcha = code)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe { it ->
                    handleCertProcess(it)
                }
    }


    private fun handleCertProcess(process: CertProcess) {
        currentProcess = process
        if (process.needSecRequest) {
            val wrapCode = process.wrapCode()
            when (wrapCode) {
                CertProcess.Code.CODE_10008 -> onRequestSuccess()
                CertProcess.Code.CODE_10001,
                CertProcess.Code.CODE_10002 -> verifyViaSms()
                CertProcess.Code.CODE_10003 -> onPasswordError()
                CertProcess.Code.CODE_10004,
                CertProcess.Code.CODE_10006 -> onSmsCodeError()
                CertProcess.Code.CODE_10022 -> verifyViaQueryPwd()
                CertProcess.Code.CODE_10007,
                CertProcess.Code.CODE_10017,
                CertProcess.Code.CODE_10018,
                CertProcess.Code.CODE_10023,
                CertProcess.Code.CODE_30000,
                CertProcess.Code.CODE_0,
                null -> onFail(wrapCode?.message)
            }
        } else {
            onRequestSuccess()
        }
    }

    private fun onFail(message: String?) {
        toast(message?:"报告申请发生错误")
    }

    private fun onSmsCodeError() {
        toast("短信验证码错误")
    }

    private fun onPasswordError() {
        toast("密码错误")
    }

    private fun verifyViaQueryPwd() {
        replaceFragment(verifyCarrierQueryPasswordFragment,R.id.fl_container)
    }

    private fun verifyViaSms() {
        replaceFragment(verifyCarrierSmsCodeFragment,R.id.fl_container)
    }

    private fun onRequestSuccess() {
        Pop.toast("认证申请提交成功",this)
        CertOperations.onCmLogRequestSuccess(submitOrderId!!,submitPhoneNo!!,submitServicePassword!!)
        finish()
    }

    private fun obtainOrderId(passport: Passport): Single<Long> {
        val orderId = submitOrderId
        return if (orderId == null) {
            CertOperations.obtainNewCmLogOrderId(passport)
                    .map { it.orderId }
                    .doOnSuccess {
                        this.submitOrderId = it
                    }
        } else {
            Single.just(orderId)
        }
    }

    private fun checkPreconditions() {
        if (!App.get().passportRepository.passportEnabled) {
            runOnMainThread {
                Pop.toast(R.string.ca_not_enabled, this)
                finish()
            }
            return
        }
        val certId = CertOperations.getCertId()
        if (certId == null) {
            runOnMainThread {
                Pop.toast("身份认证未完成", this)
                finish()
            }
            return
        }
        realName = certId.first
        realId = certId.second
        passport = App.get().passportRepository.getCurrentPassport()!!
        //enter input phone step
        replaceFragment(inputPhoneInfoFragment, R.id.fl_container)
    }
}
