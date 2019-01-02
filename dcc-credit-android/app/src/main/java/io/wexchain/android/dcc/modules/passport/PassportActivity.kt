package io.wexchain.android.dcc.modules.passport

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.repo.db.CaAuthRecord
import io.wexchain.android.dcc.view.adapter.BottomMoreItemsAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.AuthManage
import io.wexchain.android.dcc.vm.BottomMoreVm
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPassportBinding
import io.wexchain.dcc.databinding.ItemAuthRecordBinding
import io.wexchain.dcc.databinding.ItemBottomMoreTextBinding

class PassportActivity : BindActivity<ActivityPassportBinding>(),
        ItemViewClickListener<CaAuthRecord> {

    override val contentLayoutId: Int = R.layout.activity_passport

    private val adapter = SimpleDataBindAdapter<ItemAuthRecordBinding, CaAuthRecord>(
            R.layout.item_auth_record,
            BR.record,
            this
    )

    private lateinit var bm: BottomMoreVm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        setupData()
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_passport_auth, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_passport_auth -> {
                navigateTo(AuthManageActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResourceLoaded(id: Int) {
        super.onResourceLoaded(id)
        when (id) {
            R.id.iv_avatar -> {
                supportStartPostponedEnterTransition()
            }
        }
    }

    private fun setupData() {
        val repo = App.get().passportRepository
        repo.currPassport.observe(this, Observer {
            it?.let {
                binding.passport = it
                binding.statusEnabled = it.authKey != null
                supportStartPostponedEnterTransition()
            }
        })
        val llm = LinearLayoutManager(this)
        binding.rvAuthRecords.layoutManager = llm
        bm = BottomMoreVm().apply {
            noMoreHint.set(getText(R.string.no_more_records))
        }
        val bmAdapter = BottomMoreItemsAdapter(adapter, bottomViewProvider(bm))
        binding.rvAuthRecords.adapter = bmAdapter
        repo.authRecords
                .observe(this, Observer<List<CaAuthRecord>> {
                    if (it != null && it.size > 6) {
                        adapter.setList(it.subList(0, 6))
                        bm.hasMore.set(true)
                    } else {
                        adapter.setList(it)
                        bm.hasMore.set(false)
                    }
                })

    }

    private fun initData() {
        val authManage = ViewModelProviders.of(this).get(AuthManage::class.java)
        authManage.sync(this)
        authManage.loadingEvent.observe(this, Observer { loading ->
            loading?.let {
                if (it) {
                    showLoadingDialog()
                } else {
                    hideLoadingDialog()
                }
            }
        })
        authManage.successEvent.observe(this, Observer {
            toast("更新密钥成功")
        })
        authManage.errorEvent.observe(this, Observer {
            toast(it?.message ?: "出现异常")
        })
        VerifyProtectFragment.serve(authManage, this, { this.supportFragmentManager })
        binding.authManage = authManage
    }

    private fun bottomViewProvider(bm: BottomMoreVm): BottomMoreItemsAdapter.BottomViewProvider {
        return object : BottomMoreItemsAdapter.BottomViewProvider {
            override fun inflateBottomView(parent: ViewGroup): View {
                val b = DataBindingUtil.inflate<ItemBottomMoreTextBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_bottom_more_text,
                        parent,
                        false
                )
                b.bm = bm
                b.tvLoadMore.setOnClickListener {
                    //                    startActivity(Intent(this@PassportActivity, AuthRecordsActivity::class.java))
                }
                return b.root
            }

            override fun onBind(bottomView: View?, position: Int) {
            }
        }
    }


    override fun onItemClick(item: CaAuthRecord?, position: Int, viewId: Int) {
        if (bm.hasMore.get()) {
//            startActivity(Intent(this, AuthRecordsActivity::class.java))
        }
    }
}
