package worhavah.tongniucertmodule

import android.os.Bundle
import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.replaceFragment
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.CertProcess
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import worhavah.certs.bean.TNcert1
import worhavah.certs.bean.TNcertAdvance
import worhavah.certs.bean.TNcertResent
import worhavah.certs.tools.CertOperations
import worhavah.mobilecertmodule.R
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.Passport

class SubmitTNLogActivity : BaseCompatActivity(), InputPhoneInfoFragment.Listener,
    VerifyCarrierSmsCodeFragment.Listener, VerifyCarrierQueryPasswordFragment.Listener {
    override fun reCode( ):Single<String> {

       return  sentCode( ).map {
             it.auth_code
         }
            /* .observeOn(AndroidSchedulers.mainThread())
             .doOnSubscribe {
                 showLoadingDialog()
             }
             .doFinally {
                 hideLoadingDialog()
             }
             .subscribe ({ process ->
                 img= process.auth_code

             },{toast(it.message.toString())})*/
    }
    var img:String=""

    private val inputPhoneInfoFragment by lazy { InputPhoneInfoFragment.create(this) }
    private val verifyCarrierSmsCodeFragment by lazy { VerifyCarrierSmsCodeFragment.create(this) }
    private val verifyCarrierImgCodeFragment by lazy { VerifyCarrierImgCodeFragment.create(this,img) }


    private val verifyCarrierQueryPasswordFragment by lazy {
        VerifyCarrierQueryPasswordFragment.create(
            this
        )
    }

    private lateinit var passport: Passport
    private lateinit var realName: String
    private lateinit var realId: String

    private var submitOrderId: Long? = null
    private var taskStage: String? = ""

    private var submitPhoneNo: String? = null
    private var submitServicePassword: String? = null

    private var currentProcess: CertProcess? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tndefault_fragment_container2)
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
        CertOperations.confirmCertFee({ supportFragmentManager }, ChainGateway.TN_COMMUNICATION_LOG) {
            val passport = passport
            obtainNewOrderId(passport)
                    .flatMap {
                        //CertProcess
                        // CertOperations.requestCommunicationLog(passport, it, realName, realId, phoneNo, password)
                        requestTNLog(passport, it, realName, realId, phoneNo, password)

                    }
                    .doOnSubscribe {
                        showLoadingDialog()
                    }
                    .doFinally {
                        hideLoadingDialog()
                    }
                    .subscribe ({ process ->
                       // handleCertProcess(process)
                        taskStage=process.data.next_stage
                        if(process.status.subCode.contains("105")){

                            verifyViaSms()
                        }else if(process.status.subCode.contains("101")){
                            img=process.data.auth_code.toString()
                            verifyViaImg()
                        }else if(process.status.subCode.contains("137")||process.status.subCode.contains("2007")){
                            Pop.toast("认证申请提交成功",this)
                            CertOperations.onTNLogRequestSuccess(submitOrderId!!,submitPhoneNo!!,submitServicePassword!!)
                            finish()
                        }
                    },{toast(it.message.toString())})
    }
    }
    fun requestTNLog(
        passport: Passport,
        orderId: Long,
        name: String,
        id: String,
        phoneNo: String,
        password: String
    ): Single<TNcert1> {
        require(passport.authKey != null)
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        Log.e("orderId",orderId.toString())
        return  CertOperations.tnCertApi.requestCommunicationLogData(
            address = address,
            orderId = orderId,
            userName = name,
            certNo = id,
            phoneNo = phoneNo,
            password = password,
            signature = ParamSignatureUtil.sign(
                privateKey, mapOf(
                    "address" to address,
                    "orderId" to orderId.toString(),
                    "userName" to name,
                    "certNo" to id,
                    "phoneNo" to phoneNo,
                    "password" to password
                )
            )
        ).compose(Result.checked())
    }
    fun pushAdvance(authCode:String?,smsCode:String?): Single<TNcertAdvance>{
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        return  CertOperations.tnCertApi.TNAdvance(
            address = address,
            orderId = submitOrderId!!,
            taskStage = taskStage!!,
            authCode = authCode,
            smsCode = smsCode,
            signature = ParamSignatureUtil.sign(
                privateKey, mapOf(
                    "address" to address,
                    "authCode" to authCode,
                    "orderId" to submitOrderId!!.toString(),
                    "smsCode" to smsCode,
                    "taskStage" to taskStage!!
                )
            )
        ).compose(Result.checked())
    }

    override fun onSubmitSmsCode(authCode:String?,smsCode:String?) {
        pushAdvance(authCode ,smsCode ) .observeOn(AndroidSchedulers.mainThread()).doOnSubscribe {
            showLoadingDialog()
        }
            .doFinally {
                hideLoadingDialog()
            }
            .subscribe ({ process ->
                // handleCertProcess(process)
                if(process.status.subCode.contains("137")||process.status.subCode.contains("2007")){
                    Pop.toast("认证申请提交成功",this)
                    CertOperations.onTNLogRequestSuccess(submitOrderId!!,submitPhoneNo!!,submitServicePassword!!)
                    finish()
                }
            },{toast(it.message.toString())})
        /*val process = currentProcess
        if(process != null){
            when(process.wrapCode()){
                CertProcess.Code.CODE_10001,
                CertProcess.Code.CODE_10002 -> processWithSmsCode(process,code)
                else -> {
                    //illegal state
                }
            }
        }*/
    }
    fun sentCode( ):Single<TNcertResent> {
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
      return  CertOperations.tnCertApi.TNGetcode(
            address =  address,
            orderId = submitOrderId!!,
            signature = ParamSignatureUtil.sign(
                privateKey, mapOf(
                    "address" to address,
                    "orderId" to submitOrderId!!.toString()
                )
            )
        ) .compose(Result.checked())
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
                .subscribe ({ it ->
                    handleCertProcess(it)
                },{toast(it.message.toString())})
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
                .subscribe({ it ->
                    handleCertProcess(it)
                },{toast(it.message.toString())})
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
        toast(getString(R.string.wrong_password))
    }

    private fun verifyViaQueryPwd() {
        replaceFragment(verifyCarrierQueryPasswordFragment, R.id.fl_container)
    }

    private fun verifyViaSms() {
        replaceFragment(verifyCarrierSmsCodeFragment, R.id.fl_container)
    }
    private fun verifyViaImg() {
        replaceFragment(verifyCarrierImgCodeFragment, R.id.fl_container)
    }
    private fun onRequestSuccess() {
        Pop.toast("认证申请提交成功",this)
        CertOperations.onCmLogRequestSuccess(submitOrderId!!,submitPhoneNo!!,submitServicePassword!!)
        finish()
    }
    private fun obtainNewOrderId(passport: Passport): Single<Long> {
        val orderId = submitOrderId
         return   CertOperations.obtainNewCmLogOrderId(passport)
                .map { it.orderId }
                .doOnSuccess {
                    this.submitOrderId = it
                  //  Single.just(orderId)
                }


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
      /*  if (!Networkutils.passportRepository.passportEnabled) {
            runOnMainThread {
                Pop.toast(R.string.ca_not_enabled, this)
                finish()
            }
            return
        }*/
        val certId = CertOperations.getCertId()
        if (certId == null) {
            runOnMainThread {
                Pop.toast(getString(R.string.verification_first), this)
                finish()
            }
            return
        }
        realName = certId.first
        realId = certId.second
        passport = Networkutils.passportRepository.getCurrentPassport()!!
        //enter input phone step
        replaceFragment(inputPhoneInfoFragment, R.id.fl_container)
    }
}
