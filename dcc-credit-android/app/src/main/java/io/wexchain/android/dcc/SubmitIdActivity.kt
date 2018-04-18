package io.wexchain.android.dcc

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.replaceFragment
import io.wexchain.android.common.stackTrace
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.fragment.InputIdInfoFragment
import io.wexchain.android.dcc.fragment.LivenessDetectionFragment
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.auth.R
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.CertOrder

class SubmitIdActivity : BaseCompatActivity(), InputIdInfoFragment.Listener, LivenessDetectionFragment.Listener {

    private val inputIdInfoFragment by lazy { InputIdInfoFragment.create(this) }
    private val livenessDetectionFragment by lazy { LivenessDetectionFragment.create(this) }

    private var idCardEssentialData: IdCardEssentialData? = null
    private var portrait: ByteArray? = null

    override fun onProceed(idCardEssentialData: IdCardEssentialData) {
        this.idCardEssentialData = idCardEssentialData
        enterStep(STEP_LIVENESS_DETECT)
    }

    override fun onProceed(portrait: ByteArray) {
        this.portrait = portrait
        showConfirmCertFeeDialog()
    }

    private fun showConfirmCertFeeDialog() {
        CertOperations.confirmCertFee({ supportFragmentManager }, ChainGateway.BUSINESS_ID) {
            doSubmitIdCert()
        }
    }

    private fun doSubmitIdCert() {
        val idData = idCardEssentialData
        val photo = portrait
        val passport = App.get().passportRepository.getCurrentPassport()
        if (passport?.authKey != null && idData != null && photo != null) {
            CertOperations.submitIdCert(passport, idData, photo)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        showLoadingDialog()
                    }
                    .doFinally {
                        hideLoadingDialog()
                    }
                    .subscribe({
                        handleCertResult(it, idData, photo)
                    }, {
                        if (it is IllegalStateException && it.message == CertOperations.ERROR_SUBMIT_ID_NOT_MATCH) {
                            INMDialog().show(supportFragmentManager,null)
                        }
                        stackTrace(it)
                    })
        }
    }

    private fun handleCertResult(certOrder: CertOrder, idData: IdCardEssentialData, photo: ByteArray) {
        if (certOrder.status.isPassed()) {
            toast("认证成功")
            CertOperations.saveIdCertData(certOrder, idData, photo)
            finish()
        } else {
            toast("认证失败")
            supportFragmentManager.popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_fragment_container)
        initToolbar()
        if (readyToIdCert()) {
            enterStep(STEP_INPUT_ID)
        } else {
            postOnMainThread {
                toast(R.string.ca_not_enabled)
                finish()
            }
        }
    }

    private fun readyToIdCert(): Boolean {
        return App.get().passportRepository.passportEnabled
    }

    private fun enterStep(step: Int) {
        when (step) {
            STEP_INPUT_ID ->
                replaceFragment(inputIdInfoFragment, R.id.fl_container)
            STEP_LIVENESS_DETECT ->
                replaceFragment(livenessDetectionFragment, R.id.fl_container, backStackStateName = "step_$step")
        }
    }

    class INMDialog : DialogFragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val view = inflater.inflate(R.layout.dialog_notice_id_not_match, container, false)
            view.findViewById<View>(R.id.btn_confirm).setOnClickListener { dismiss() }
            return view
        }
    }

    companion object {
        private const val STEP_INPUT_ID = 0
        private const val STEP_LIVENESS_DETECT = 1
    }
}
