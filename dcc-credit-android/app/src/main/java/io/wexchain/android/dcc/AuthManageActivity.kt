package io.wexchain.android.dcc

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.common.Pop
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.AuthKeyChangeRecord
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.android.dcc.vm.AuthManage
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityAuthManageBinding
import io.wexchain.dcc.databinding.ItemAuthkeyChangeRecordBinding
import io.wexchain.dccchainservice.DccChainServiceException

class AuthManageActivity : BindActivity<ActivityAuthManageBinding>() {
    override val contentLayoutId: Int = R.layout.activity_auth_manage

    private val adapter = Adapter()

    private val fromAuth by lazy { intent.getBooleanExtra(Extras.FROM_AUTH, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        initData()
        initClicks()
        checkFrom()
    }

    private fun initClicks() {
//        binding.ivAuthIntro.setOnClickListener {
//            CustomDialog(this).apply {
//                textContent = getString(R.string.auth_key_intro)
//            }.assembleAndShow()
//        }
    }

    private fun checkFrom() {
        if (fromAuth) {
            binding.authManage!!.switchPassportLoginEnable()
        }
    }

    private fun initData() {
        val authManage = ViewModelProviders.of(this).get(AuthManage::class.java)
        authManage.sync(this)
        binding.rvAuthChangeRecords.adapter = adapter
        authManage.loadingEvent.observe(this, Observer { loading ->
            loading?.let {
                if (it) {
                    showLoadingDialog()
                } else {
                    hideLoadingDialog()
                }
            }
        })
        VerifyProtectFragment.serve(authManage, this, { this.supportFragmentManager })
        authManage.changeRecords.observe(this, Observer {
            it?.let {
                adapter.setList(it)
            }
        })
        authManage.authKeyChangeEvent.observe(this, Observer {
            if (fromAuth && it == AuthKeyChangeRecord.UpdateType.ENABLE) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
        authManage.errorEvent.observe(this, Observer {
            it?.let {
                if(it is DccChainServiceException && !it.message.isNullOrBlank()){
                    Pop.toast(it.message!!,this)
                }else {
                    if (BuildConfig.DEBUG) it.printStackTrace()
                }
            }
        })
        binding.authManage = authManage
    }

    private class Adapter : DataBindAdapter<ItemAuthkeyChangeRecordBinding, AuthKeyChangeRecord>(
        R.layout.item_authkey_change_record,
        defaultItemDiffCallback()
    ) {
        override fun bindData(binding: ItemAuthkeyChangeRecordBinding, item: AuthKeyChangeRecord?) {
            binding.pa = item
        }

    }
}
