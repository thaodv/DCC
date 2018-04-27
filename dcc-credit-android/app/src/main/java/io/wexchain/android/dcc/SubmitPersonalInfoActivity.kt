package io.wexchain.android.dcc

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.vm.SubmitPersonalInfoVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivitySubmitPersonalInfoBinding

class SubmitPersonalInfoActivity : BindActivity<ActivitySubmitPersonalInfoBinding>() {
    override val contentLayoutId: Int = R.layout.activity_submit_personal_info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.vm = ViewModelProviders.of(this)[SubmitPersonalInfoVm::class.java]
    }
}
