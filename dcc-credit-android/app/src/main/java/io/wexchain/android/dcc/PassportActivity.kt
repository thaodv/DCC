package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.common.withTransitionEnabled
import io.wexchain.android.dcc.constant.Transitions
import io.wexchain.android.dcc.repo.db.CaAuthRecord
import io.wexchain.android.dcc.view.adapter.BottomMoreItemsAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.BottomMoreVm
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
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        toolbarTitle = findViewById<TextView>(R.id.toolbar_title).apply {
            text = title
        }
        setupData()
        setupBtn()
        setupTransitions()
        supportPostponeEnterTransition()
    }

    override fun onResourceLoaded(id: Int) {
        super.onResourceLoaded(id)
        when(id){
            R.id.iv_avatar->{
                supportStartPostponedEnterTransition()
            }
        }
    }

    private fun setupTransitions() {
        withTransitionEnabled {
            // avoid leaks
            ViewCompat.setTransitionName(findViewById(R.id.card_passport), Transitions.CARD_PASSPORT)
            ViewCompat.setTransitionName(findViewById(R.id.iv_avatar), Transitions.CARD_PASSPORT_AVATAR)
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
//            hasMoreIcon.set(ContextCompat.getDrawable(this@PassportActivity, R.drawable.more_items))
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

    private fun setupBtn() {
        binding.ibClose.setOnClickListener {
            goFinish()
        }
        binding.btnToManageAuth.setOnClickListener {
            startActivity(Intent(this, AuthManageActivity::class.java))
        }
        binding.cardPassport!!.tvPassportAddress.setOnClickListener {
            startActivity(Intent(this, PassportAddressActivity::class.java))
        }
    }

    override fun onItemClick(item: CaAuthRecord?, position: Int, viewId: Int) {
        if (bm.hasMore.get()) {
//            startActivity(Intent(this, AuthRecordsActivity::class.java))
        }
    }
}
