package io.wexchain.android.dcc

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.android.dcc.vm.SubmitPersonalInfoVm
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivitySubmitPersonalInfoBinding

class SubmitPersonalInfoActivity : BindActivity<ActivitySubmitPersonalInfoBinding>() {
    override val contentLayoutId: Int = R.layout.activity_submit_personal_info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = ViewModelProviders.of(this)[SubmitPersonalInfoVm::class.java]
    }
}
