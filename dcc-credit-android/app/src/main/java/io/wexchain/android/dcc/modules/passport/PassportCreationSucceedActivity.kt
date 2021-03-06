package io.wexchain.android.dcc.modules.passport

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.modules.home.HomeActivity
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.fragment.CreateProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPassportCreationSucceedBinding

class PassportCreationSucceedActivity: BindActivity<ActivityPassportCreationSucceedBinding>() {
    override val contentLayoutId: Int = R.layout.activity_passport_creation_succeed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnEnableProtect.setOnClickListener {
            showEnableProtectDialog()
        }
        binding.btnSkip.setOnClickListener { skip() }
        setupData()
        App.get().initcerts()
    }


    private fun showEnableProtectDialog() {
        CreateProtectFragment.create(object :CreateProtectFragment.CreateProtectFinishListener{
            override fun onCancel(): Boolean {
                skip()
                return true
            }

            override fun cancelText(): String {
                return getString(R.string.skip)
            }


            override fun onCreateProtectFinish(type: LocalProtectType) {
                toast(R.string.local_protect_enabled)
                createProtectDone(type)
            }
        }).show(supportFragmentManager, null)
    }

    private fun createProtectDone(type: LocalProtectType?) {
        navigateTo(HomeActivity::class.java)
        finish()
    }


    private fun setupData() {
        binding.fromImport = intent.getBooleanExtra(Extras.FROM_IMPORT, false)
        App.get().passportRepository.currPassport
                .observe(this, Observer {
                    it?.let {
                        binding.passport = it
                    }
                })
    }
    private fun skip(){
        createProtectDone(null)
    }
}
