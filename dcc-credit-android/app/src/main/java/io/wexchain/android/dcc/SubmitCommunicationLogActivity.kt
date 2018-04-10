package io.wexchain.android.dcc

import android.os.Bundle
import io.reactivex.Single
import io.wexchain.android.common.Pop
import io.wexchain.android.common.replaceFragment
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.fragment.InputPhoneInfoFragment
import io.wexchain.auth.R
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.CertProcess

class SubmitCommunicationLogActivity : BaseCompatActivity(), InputPhoneInfoFragment.Listener {

    private val inputPhoneInfoFragment by lazy { InputPhoneInfoFragment.create(this)}

    private lateinit var passport:Passport
    private lateinit var realName: String
    private lateinit var realId: String

    private var submitOrderId:Long? = null
    private var submitPhoneNo:String? = null
    private var submitServicePassword:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_fragment_container)
        initToolbar()
        checkPreconditions()
    }

    override fun onSubmitPhoneInfo(phoneNo: String, password: String) {
        this.submitPhoneNo = phoneNo
        this.submitServicePassword = password
        CertOperations.confirmCertFee({supportFragmentManager},ChainGateway.BUSINESS_COMMUNICATION_LOG){
            val passport = passport
            obtainOrderId(passport)
                    .flatMap {
                        CertOperations.requestCommunicationLog(passport,it,realName,realId,phoneNo,password)
                    }
                    .doOnSubscribe {
                        showLoadingDialog()
                    }
                    .doFinally {
                        hideLoadingDialog()
                    }
                    .subscribe{process->
                        handleCertProcess(process)
                    }
        }
    }

    private fun handleCertProcess(process: CertProcess) {
        if (process.needSecRequest){
            when(process.wrapCode()){
                CertProcess.Code.CODE_10008 -> onRequestSuccess()
                CertProcess.Code.CODE_10001 ,
                CertProcess.Code.CODE_10002 -> verifyViaSms()
                CertProcess.Code.CODE_10003 -> onPasswordError()
                CertProcess.Code.CODE_10004,
                CertProcess.Code.CODE_10006 -> onSmsCodeError()
                CertProcess.Code.CODE_10007 ,
                CertProcess.Code.CODE_10017 ,
                CertProcess.Code.CODE_10018 ,
                CertProcess.Code.CODE_10022 ,
                CertProcess.Code.CODE_10023 ,
                CertProcess.Code.CODE_30000 ,
                CertProcess.Code.CODE_0 ,
                null -> onFail(process.wrapCode()?.message)
            }
        }else{

        }
    }

    private fun onFail(message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onSmsCodeError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onPasswordError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun verifyViaSms() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onRequestSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun obtainOrderId(passport: Passport): Single<Long>{
        val orderId = submitOrderId
        return if (orderId == null){
            CertOperations.obtainNewCmLogOrderId(passport)
                    .map { it.orderId }
                    .doOnSuccess {
                        this.submitOrderId = it
                    }
        }else{
            Single.just(orderId)
        }
    }

    private fun checkPreconditions() {
        if(!App.get().passportRepository.passportEnabled){
            runOnMainThread {
                Pop.toast("通行证未启用", App.get())
                finish()
            }
            return
        }
        val certId = CertOperations.getCertId()
        if (certId == null) {
            runOnMainThread {
                Pop.toast("身份认证未完成", App.get())
                finish()
            }
            return
        }
        realName = certId.first
        realId = certId.second
        passport = App.get().passportRepository.getCurrentPassport()!!
        //enter input phone step
        replaceFragment(inputPhoneInfoFragment,R.id.fl_container)
    }
}
