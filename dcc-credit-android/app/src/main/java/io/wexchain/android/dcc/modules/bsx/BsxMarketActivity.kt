package io.wexchain.android.dcc.modules.bsx

import android.os.Bundle
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxMarketBinding
import io.wexchain.dcc.databinding.ItemBsxMarketBinding
import io.wexchain.dccchainservice.domain.BsxMarketBean

class BsxMarketActivity : BindActivity<ActivityBsxMarketBinding>(), ItemViewClickListener<BsxMarketBean> {

    override val contentLayoutId: Int
        get() = R.layout.activity_bsx_market

    private val adapter = Adapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        binding.ivNext.setOnClickListener { navigateTo(BsxHoldingActivity::class.java) }

        binding.rvList.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        App.get().scfApi.getBsxMarketList().checkonMain().subscribe({
            binding.records = it
        }, {
            toast(it.message ?: "系统错误")
        })
    }

    override fun onItemClick(item: BsxMarketBean?, position: Int, viewId: Int) {

    }

    private class Adapter(itemViewClickListener: ItemViewClickListener<BsxMarketBean>) :
            DataBindAdapter<ItemBsxMarketBinding, BsxMarketBean>(
                    layout = R.layout.item_bsx_market,
                    itemDiffCallback = defaultItemDiffCallback(),
                    itemViewClickListener = itemViewClickListener
            ) {
        override fun bindData(binding: ItemBsxMarketBinding, item: BsxMarketBean?) {
            binding.bsxMarketBean = item
        }

    }
}
