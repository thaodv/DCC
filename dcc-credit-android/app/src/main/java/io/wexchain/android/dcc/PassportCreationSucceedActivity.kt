package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.constant.Extras
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