package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.vm.EditNicknameVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityEditNicknameBinding

class EditNicknameActivity : BindActivity<ActivityEditNicknameBinding>() {
    override val contentLayoutId: Int = R.layout.activity_edit_nickname

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        val vm = getViewModel<EditNicknameVm>()
        vm.encurePassport(App.get().passportRepository.getCurrentPassport()!!)
        vm.changeSubmittedEvent.observe(this, Observer {
            it?.let { onNicknameChanged(it) }
        })
        binding.vm = vm
    }

    private fun onNicknameChanged(newNickname: String) {
        toast(getString(R.string.user_alias_changed))
        finish()
    }
}
