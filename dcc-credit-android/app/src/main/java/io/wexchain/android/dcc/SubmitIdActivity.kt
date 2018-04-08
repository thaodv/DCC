package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.replaceFragment
import io.wexchain.android.common.stackTrace
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.fragment.InputIdInfoFragment
import io.wexchain.android.dcc.fragment.LivenessDetectionFragment
import io.wexchain.android.dcc.view.dialog.CertFeeConfirmDialog
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.auth.R

class SubmitIdActivity : BaseCompatActivity(), InputIdInfoFragment.Listener, LivenessDetectionFragment.Listener {

    private val inputIdInfoFragment by lazy { InputIdInfoFragment.create(this) }
    private val livenessDetectionFragment by lazy { LivenessDetectionFragment() }

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
        CertFeeConfirmDialog.create {
            doSubmitIdCert()
        }.show(supportFragmentManager, null)
    }

    private fun doSubmitIdCert() {
        val idData = idCardEssentialData
        val photo = portrait
        val passport = App.get().passportRepository.getCurrentPassport()
        if (passport?.authKey != null && idData != null && photo != null) {
            CertOperations.submitIdCert(passport,idData,photo)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        showLoadingDialog()
                    }
                    .doFinally {
                        hideLoadingDialog()
                    }
                    .subscribe({
                        println(it)
                        finish()
                    },{
                        stackTrace(it)
                    })
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
                toast("通行证未启用")
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

    companion object {
        private const val STEP_INPUT_ID = 0
        private const val STEP_LIVENESS_DETECT = 1
    }
}
