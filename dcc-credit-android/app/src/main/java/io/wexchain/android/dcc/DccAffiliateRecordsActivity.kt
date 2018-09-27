package io.wexchain.android.dcc

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemDccAffiliateRecordBinding
import io.wexchain.dccchainservice.domain.ScfAccountInfo

class DccAffiliateRecordsActivity : BaseCompatActivity() {


    private val adapter = SimpleDataBindAdapter<ItemDccAffiliateRecordBinding, ScfAccountInfo>(
        layoutId = R.layout.item_dcc_affiliate_record,
        variableId = BR.member
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dcc_affiliate_records)
        initToolbar()
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
        initData()
    }

    private fun initData() {
        ScfOperations
            .withScfTokenInCurrentPassport(emptyList()) {
                App.get().scfApi.invitedList(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                showLoadingDialog()
            }
            .doFinally {
                hideLoadingDialog()
            }
            .subscribe{list->
                adapter.setList(list)
                findViewById<View>(R.id.tv_no_records).visibility = if(list.isEmpty()) View.VISIBLE else View.GONE
            }
    }
}
