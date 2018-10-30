package worhavah.tongniucertmodule

import android.os.Bundle
import android.text.TextUtils
import com.githang.statusbar.StatusBarCompat
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.*
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.CertProcess
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import worhavah.certs.bean.TNcert1new
import worhavah.certs.tools.CertOperations
import worhavah.certs.tools.CertOperations.clearTNCertCache
import worhavah.mobilecertmodule.R
import worhavah.regloginlib.Net.Networkutils
import worhavah.regloginlib.Passport

class SubmitTNLogActivity : BaseCompatActivity(), InputPhoneInfoFragment.Listener,
        VerifyCarrierSmsCodeFragment.Listener, VerifyCarrierQueryPasswordFragment.Listener {
    override fun reCode(): Single<String> {

        return sentCode().map {
            it.tongniuData.authCode
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

    var img: String = ""

    private val inputPhoneInfoFragment by lazy { InputPhoneInfoFragment.create(this) }
    private val verifyCarrierSmsCodeFragment by lazy { VerifyCarrierSmsCodeFragment.create(this) }
    private val verifyCarrierImgCodeFragment by lazy { VerifyCarrierImgCodeFragment.create(this, img) }


    private val verifyCarrierQueryPasswordFragment by lazy {
        VerifyCarrierQueryPasswordFragment.create(
                this
        )
    }

    private var passport = Networkutils.passportRepository.getCurrentPassport()!!
    private lateinit var realName: String
    private lateinit var realId: String

    private var submitOrderId: Long? = -1L
    private var taskStage: String = ""

    private var submitPhoneNo: String? = ""
    private var submitServicePassword: String? = ""

    private var currentProcess: CertProcess? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tndefault_fragment_container2)
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.white))
        initToolbar(true, true)
        checkPreconditions()
        worhavah.certs.tools.CertOperations.certPrefs.certTNcertID.get().apply {
            if (this == -1L) {
            } else {
                checkCreat()
                submitPhoneNo = worhavah.certs.tools.CertOperations.certPrefs.certTNcertphoneNo.get()!!
                submitOrderId = worhavah.certs.tools.CertOperations.certPrefs.certTNcertID.get()
            }
        }
    }


    override fun goBack(): Boolean {
        val curr = supportFragmentManager.findFragmentById(R.id.fl_container)
        return if (curr != inputPhoneInfoFragment) {
            replaceFragment(inputPhoneInfoFragment, R.id.fl_container)
            true
        } else {
            super.goBack()
        }
    }

    override fun onSubmitPhoneInfo(phoneNo: String, password: String) {
        CertOperations.confirmCertFee({ supportFragmentManager }, ChainGateway.TN_COMMUNICATION_LOG) {
            val passport = passport
            val privateKey = passport.authKey!!.getPrivateKey()
            val address = passport.address
            var name = realName
            var id = realId
            submitPhoneNo = phoneNo
            submitServicePassword = password
            //var orderiiid=  obtainNewOrderId(passport).blockingGet()
            obtainNewOrderId(passport, phoneNo).subscribeOn(AndroidSchedulers.mainThread()).doOnSubscribe {
                showLoadingDialog()
            }
                    .doFinally {
                    }
                    .subscribe(
                            {
                                val signature = ParamSignatureUtil.sign(
                                        privateKey, mapOf(
                                        "address" to address,
                                        "orderId" to it.toString(),
                                        "userName" to name,
                                        "certNo" to id,
                                        "phoneNo" to phoneNo,
                                        "password" to password,
                                        "nonce" to CertOperations.TNnonce
                                )
                                )
                                worhavah.certs.tools.CertOperations.certPrefs.certTNcertaddress.set(address)
                                worhavah.certs.tools.CertOperations.certPrefs.certTNcertID.set(it)
                                worhavah.certs.tools.CertOperations.certPrefs.certTNcertuserName.set(name)
                                worhavah.certs.tools.CertOperations.certPrefs.certTNcertcertNo.set(id)
                                worhavah.certs.tools.CertOperations.certPrefs.certTNcertphoneNo.set(phoneNo)
                                worhavah.certs.tools.CertOperations.certPrefs.certTNcertpassword.set(password)
                                worhavah.certs.tools.CertOperations.certPrefs.certTNcertnonce.set(
                                        CertOperations.TNnonce
                                )
                                worhavah.certs.tools.CertOperations.certPrefs.ertTNcertsignature.set(signature)
                                checkCreat()
                            }, {
                        toast(it.message.toString())
                        hideLoadingDialog()

                    }
                    )
        }
    }

    fun checkCreat() {
        requestTNLog2(
                worhavah.certs.tools.CertOperations.certPrefs.certTNcertaddress.get()!!,
                worhavah.certs.tools.CertOperations.certPrefs.certTNcertID.get(),
                worhavah.certs.tools.CertOperations.certPrefs.certTNcertuserName.get()!!,
                worhavah.certs.tools.CertOperations.certPrefs.certTNcertcertNo.get()!!,
                worhavah.certs.tools.CertOperations.certPrefs.certTNcertphoneNo.get()!!,
                worhavah.certs.tools.CertOperations.certPrefs.certTNcertpassword.get()!!,
                worhavah.certs.tools.CertOperations.certPrefs.certTNcertnonce.get()!!,
                worhavah.certs.tools.CertOperations.certPrefs.ertTNcertsignature.get()!!
        ).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    // hideLoadingDialog()
                }
                .subscribe({ process ->
                    // handleCertProcess(process)
                    val statuss = process.endorserOrder.status
                    if (statuss.contains("COMPELETED")) {
                        hideLoadingDialog()
                        Pop.toast("认证申请提交成功", this)
                        clearTNCertCache()
                        CertOperations.onTNLogRequestSuccess(
                                submitOrderId!!,
                                submitPhoneNo!!,
                                submitServicePassword!!
                        )
                        setResult(666)
                        finish()
                    }
                    if (null != process.tongniuData) {
                        if (null != process.tongniuData.nextStage) {
                            taskStage = process.tongniuData.nextStage
                        }
                        if (statuss.contains("COMPELETED") || process.tongniuData.subCode.contains("137") || process.tongniuData.subCode.contains(
                                        "2007"
                                )
                        ) {
                            Pop.toast("认证申请提交成功", this)
                            CertOperations.onTNLogRequestSuccess(
                                    submitOrderId!!,
                                    submitPhoneNo!!,
                                    submitServicePassword!!
                            )
                            setResult(666)
                            finish()
                        }
                        if (statuss.contains("CREATED") && (null == taskStage)) {
                            checkCreat()
                        } else {
                            hideLoadingDialog()

                            if (statuss.contains("INVALID")) {
                                toast("认证失败")
                                clearTNCertCache()
                            } else {
                                if (statuss.contains("COMPELETED") || process.tongniuData.subCode.contains("137") || process.tongniuData.subCode.contains(
                                                "2007"
                                        )
                                ) {
                                    Pop.toast("认证申请提交成功", this)
                                    CertOperations.onTNLogRequestSuccess(
                                            submitOrderId!!,
                                            submitPhoneNo!!,
                                            submitServicePassword!!
                                    )
                                    setResult(666)
                                    finish()
                                }
                                if (!TextUtils.isEmpty(taskStage)) {
                                    if (process.tongniuData.subCode.contains("105")) {
                                        verifyViaSms()
                                        toast("请输入短信验证码")
                                    } else if (process.tongniuData.subCode.contains("101")) {
                                        img = process.tongniuData.authCode.toString()
                                        verifyViaImg()
                                        toast("请输入图形验证码")
                                    }
                                }
                            }
                        }
                    }
                }, {
                    hideLoadingDialog()
                    toast(it.message.toString())
                    clearTNCertCache()
                })
    }


    fun requestTNLog2(
            address: String,
            orderId: Long,
            name: String,
            id: String,
            phoneNo: String,
            password: String,
            nonce: String,
            signature: String
    ): Single<TNcert1new> {
        submitOrderId = orderId

        //   Log.e("orderId",orderId.toString())
        return CertOperations.tnCertApi.requestCommunicationLogData(
                address = address,
                orderId = orderId,
                userName = name,
                certNo = id,
                phoneNo = phoneNo,
                password = password,
                nonce = nonce,
                signature = signature
        ).compose(Result.checked())
    }

    /* fun requestTNLog(
         passport: Passport,
         orderId: Long,
         name: String,
         id: String,
         phoneNo: String,
         password: String
     ): Single<TNcert1new> {
        // require(passport.authKey != null)
         val address = passport.address
         val privateKey = passport.authKey!!.getPrivateKey()
       //  Log.e("orderId",orderId.toString())
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
     }*/
    fun pushAdvance(authCode: String?, smsCode: String?): Single<TNcert1new> {
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        return CertOperations.tnCertApi.TNAdvance(
                address = address,
                orderId = submitOrderId!!,
                taskStage = taskStage,
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

    override fun onSubmitSmsCode(authCode: String?, smsCode: String?) {
        pushAdvance(authCode, smsCode).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe {
            showLoadingDialog()
        }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe({ process ->
                    // handleCertProcess(process)
                    val statuss = process.endorserOrder.status
                    if (statuss.contains("COMPELETED")) {
                        hideLoadingDialog()
                        Pop.toast("认证申请提交成功", this)
                        clearTNCertCache()
                        CertOperations.onTNLogRequestSuccess(
                                submitOrderId!!,
                                submitPhoneNo!!,
                                submitServicePassword!!
                        )
                        setResult(666)
                        finish()
                    }
                    if (null != process.tongniuData) {
                        if (null != process.tongniuData.nextStage) {
                            taskStage = process.tongniuData.nextStage
                        }
                        if (statuss.contains("STARTED")) {
                            Pop.toast("认证申请提交成功", this)
                            CertOperations.onTNLogRequestSuccess(
                                    submitOrderId!!,
                                    submitPhoneNo!!,
                                    submitServicePassword!!
                            )
                            setResult(666)
                            finish()
                        } else if (statuss.contains("CREATED") && (null == taskStage) && process.tongniuData.subCode.contains(
                                        "100"
                                )
                        ) {
                            checkCreat()
                        } else if (process.tongniuData.subCode.contains("105")) {
                            verifyViaSms()
                            toast("请再次输入短信验证码")

                        } else if (process.tongniuData.subCode.contains("101")) {
                            img = process.tongniuData.authCode.toString()
                            verifyViaImg()
                            toast("请再次输入图形验证码")
                        } else {
                            clearTNCertCache()
                            toast(process.tongniuData.msg.toString())
                        }
                    }


                    /*if(process.tongniuData.subCode.contains("137")||process.tongniuData.subCode.contains("2007")){
                        Pop.toast("认证申请提交成功",this)
                        CertOperations.onTNLogRequestSuccess(submitOrderId!!,submitPhoneNo!!,submitServicePassword!!)
                        finish()
                    }*/
                }, {
                    clearTNCertCache()
                    toast(it.message.toString())
                })
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

    fun sentCode(): Single<TNcert1new> {
        val address = passport.address
        val privateKey = passport.authKey!!.getPrivateKey()
        return CertOperations.tnCertApi.TNGetcode(
                address = address,
                orderId = submitOrderId!!,
                signature = ParamSignatureUtil.sign(
                        privateKey, mapOf(
                        "address" to address,
                        "orderId" to submitOrderId!!.toString()
                )
                )
        ).compose(Result.checked())
    }


    override fun onSubmitQueryPwd(code: String) {
        val process = currentProcess
        if (process != null && process.wrapCode() == CertProcess.Code.CODE_10022) {
            processWithQueryPwd(process, code)
        }
    }

    private fun processWithQueryPwd(process: CertProcess, code: String) {
        val passport = passport
        val phoneNo = submitPhoneNo!!
        val password = submitServicePassword!!
        obtainOrderId(passport)
                .flatMap {
                    CertOperations.advanceCommunicationLog(passport, it, realName, realId, phoneNo, password, process, queryPwd = code)
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
                }, { toast(it.message.toString()) })
    }

    private fun processWithSmsCode(process: CertProcess, code: String) {
        val passport = passport
        val phoneNo = submitPhoneNo!!
        val password = submitServicePassword!!
        obtainOrderId(passport)
                .flatMap {
                    CertOperations.advanceCommunicationLog(passport, it, realName, realId, phoneNo, password, process, captcha = code)
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
                }, { toast(it.message.toString()) })
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
        toast(message ?: "报告申请发生错误")
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
        Pop.toast("认证申请提交成功", this)
        CertOperations.onCmLogRequestSuccess(submitOrderId!!, submitPhoneNo!!, submitServicePassword!!)
        setResult(666)
        finish()
    }

    private fun obtainNewOrderId(passport: Passport, pn: String): Single<Long> {
        val orderId = submitOrderId
        return CertOperations.obtainNewCmLogUpdateOrderId(passport, pn)
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

        if (!Networkutils.passportRepository.passportEnabled) {
            Networkutils.passportRepository.load()
            passport = Networkutils.passportRepository.getCurrentPassport()!!
        }
        if (null == getAndroidKeyStoreLoaded().getEntry(passport.authKey!!.keyAlias, null)) {
            Networkutils.passportRepository.load()
            passport = Networkutils.passportRepository.getCurrentPassport()!!
        }
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
