package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.replaceFragment
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.fragment.InputIdInfoFragment
import io.wexchain.android.idverify.IdCardEssentialData
import io.wexchain.auth.R

class SubmitIdActivity : BaseCompatActivity(), InputIdInfoFragment.Listener {

    private var idCardEssentialData:IdCardEssentialData? = null

    override fun onProceed(idCardEssentialData: IdCardEssentialData) {
        this.idCardEssentialData = idCardEssentialData
        enterStep(STEP_LIVENESS_DETECT)
    }

    private val inputIdInfoFragment by lazy { InputIdInfoFragment.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_fragment_container)
        initToolbar()
        if (App.get().passportRepository.passportEnabled){
            enterStep(STEP_INPUT_ID)
        }else{
            postOnMainThread {
                toast("通行证未启用")
                finish()
            }
        }
    }

    private fun enterStep(step: Int) {
        when(step){
            STEP_INPUT_ID ->
                replaceFragment(inputIdInfoFragment,R.id.fl_container)
            STEP_LIVENESS_DETECT->
                replaceFragment(inputIdInfoFragment,R.id.fl_container,backStackStateName = "step_$step")
        }
    }

    companion object {
        private const val STEP_INPUT_ID = 0
        private const val STEP_LIVENESS_DETECT = 1
    }
}
