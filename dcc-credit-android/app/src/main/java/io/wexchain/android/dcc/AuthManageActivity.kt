package io.wexchain.android.dcc

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import io.wexchain.android.common.Pop
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.repo.db.AuthKeyChangeRecord
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.android.dcc.vm.AuthManage
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityAuthManageBinding
import io.wexchain.dcc.databinding.ItemAuthkeyChangeRecordBinding
import io.wexchain.dccchainservice.DccChainServiceException

class AuthManageActivity : BindActivity<ActivityAuthManageBinding>(), ItemViewClickListener<AddressBook> {

    override fun onItemClick(item: AddressBook?, position: Int, viewId: Int) {

    }

    override val contentLayoutId: Int = R.layout.activity_auth_manage

    private val adapter = Adapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        initData()
    }

    private fun initData() {
        val authManage = ViewModelProviders.of(this).get(AuthManage::class.java)
        authManage.sync(this)
        binding.rvAuthChangeRecords.adapter = adapter
        authManage.changeRecords.observe(this, Observer {
            it?.let {
                adapter.setList(it)
            }
        })
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
