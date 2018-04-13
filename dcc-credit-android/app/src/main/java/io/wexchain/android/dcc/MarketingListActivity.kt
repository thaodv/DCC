package io.wexchain.android.dcc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.auth.BR
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ItemMarketingActivityBinding
import io.wexchain.dccchainservice.domain.MarketingActivity
import io.wexchain.dccchainservice.domain.Result

class MarketingListActivity : BaseCompatActivity(), ItemViewClickListener<MarketingActivity> {

    private val adapter = SimpleDataBindAdapter<ItemMarketingActivityBinding,MarketingActivity>(
            layoutId = R.layout.item_marketing_activity,
            variableId = BR.ma,
            itemViewClickListener = this@MarketingListActivity
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_list)
        initToolbar()
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
        loadMa()
    }

    override fun onItemClick(item: MarketingActivity?, position: Int, viewId: Int) {
        item?.let {
            navigateTo(MarketingScenariosActivity::class.java){
                putExtra(Extras.EXTRA_MARKETING_ACTIVITY,it)
            }
        }
    }

    private fun loadMa() {
        App.get().marketingApi.queryActivity()
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list->
                    adapter.setList(list)
                }
    }
}
