package io.wexchain.android.dcc.modules.cert

import android.os.Bundle
import android.os.SystemClock
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.*
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.fragment.cert.InputBankCardInfoFragment
import io.wexchain.android.dcc.fragment.cert.VerifyBankSmsCodeFragment
import io.wexchain.android.dcc.modules.cashloan.act.CashCertificationActivity
import io.wexchain.android.dcc.vm.domain.BankCardInfo
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.CertOrder

class SubmitBankCardActivity : BaseCompatActivity(), InputBankCardInfoFragment.Listener, VerifyBankSmsCodeFragment.Listener {

    private val inputBankCardInfoFragment by lazy { InputBankCardInfoFragment.create(this) }

    private val verifyBankSmsCodeFragment by lazy { VerifyBankSmsCodeFragment.create(this) }

    private var bankCardInfo: BankCardInfo? = null

    private var submitOrderId: Long? = null
    private var submitTicket: String? = null
    private var submitTicketUpTimeStamp: Long? = null

    private lateinit var passport: Passport
    private lateinit var realName: String
    private lateinit var realId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_fragment_container)
        initToolbar()
        checkPreconditions()
    }

    private fun checkPreconditions() {
        val cp = App.get().passportRepository.getCurrentPassport()
        if (cp?.authKey == null) {
            runOnMainThread {
                Pop.toast(R.string.ca_not_enabled, this@SubmitBankCardActivity)
                finish()
            }
            return
        }
        val certId = CertOperations.getCertId()
        if (certId == null) {
            runOnMainThread {
                Pop.toast(getString(R.string.verification_first), this@SubmitBankCardActivity)
                finish()
            }
            return
        }
        realName = certId.first
        realId = certId.second
        passport = cp
        enterStep(STEP_INPUT_BANK_CARD_INFO)
    }

    override fun onProceed(bankCardInfo: BankCardInfo) {
        this.bankCardInfo = bankCardInfo
        proceedSubmit(bankCardInfo)
    }

    override fun onSubmitSmsCode(code: String) {
        doAdvance(code)
    }

    override fun requestReSendSmsCode() {
        val info = bankCardInfo!!
        val orderId = submitOrderId!!
        CertOperations.verifyBankCardCert(passport, info, realName, realId, orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    setSmsTicket(it)
                    toast("已重发验证码")
                }
    }

    private fun enterStep(step: Int) {
        when (step) {
            STEP_INPUT_BANK_CARD_INFO -> {
                val type = intent.getStringExtra("type")
                if (CashCertificationActivity.CERT_TYPE_CASHLOAN == type) {
                    inputBankCardInfoFragment.certType = type
                }
                replaceFragment(inputBankCardInfoFragment, R.id.fl_container)
            }
            STEP_VERIFY_BANK_CARD_SMS_CODE -> {

                verifyBankSmsCodeFragment.phoneNum = bankCardInfo!!.phoneNo
                replaceFragment(verifyBankSmsCodeFragment, R.id.fl_container, backStackStateName = "step_${STEP_VERIFY_BANK_CARD_SMS_CODE}")
            }
        }
    }

    private fun proceedSubmit(info: BankCardInfo?) {
        requireNotNull(info)
        info!!
        CertOperations.confirmCertFee({ supportFragmentManager }, ChainGateway.BUSINESS_BANK_CARD) {
            submitVerify(passport, info)
        }
    }

    private fun submitVerify(passport: Passport, info: BankCardInfo) {
        CertOperations.submitBankCardCert(passport, info, realName, realId) { this.submitOrderId = it }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe({ it ->
                    setSmsTicket(it)
                    enterStep(STEP_VERIFY_BANK_CARD_SMS_CODE)
                })
    }

    private fun setSmsTicket(it: String?) {
        this.submitTicket = it
        val uptimeMillis = SystemClock.uptimeMillis()
        this.submitTicketUpTimeStamp = uptimeMillis
        verifyBankSmsCodeFragment.smsUpTimeStamp = uptimeMillis
    }

    private fun doAdvance(code: String) {
        val authKey = passport.authKey
        requireNotNull(authKey)
        authKey!!
        val orderId = submitOrderId
        requireNotNull(orderId)
        orderId!!
        val ticket = submitTicket
        requireNotNull(ticket)
        ticket!!
        CertOperations.advanceBankCardCert(passport, orderId, ticket, code)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe { it ->
                    handleCertResult(it, bankCardInfo!!)
                }
    }

    private fun handleCertResult(certOrder: CertOrder, bankCardInfo: BankCardInfo) {
        if (certOrder.status.isPassed()) {
            toast("认证成功")
            CertOperations.saveBankCertData(certOrder, bankCardInfo)
            worhavah.certs.tools.CertOperations.certed()
            finish()
        } else {
            toast(getString(R.string.verification_failed))
            supportFragmentManager.popBackStack()
        }
    }


    companion object {
        const val STEP_INPUT_BANK_CARD_INFO = 0
        const val STEP_VERIFY_BANK_CARD_SMS_CODE = 1
    }
}
