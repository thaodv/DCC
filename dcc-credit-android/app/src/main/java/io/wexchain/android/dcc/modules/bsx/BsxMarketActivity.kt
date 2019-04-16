package io.wexchain.android.dcc.modules.bsx

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.StringUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxMarketBinding
import io.wexchain.dcc.databinding.ItemBsxMarketBinding
import io.wexchain.dccchainservice.domain.BsxMarketBean
import io.wexchain.ipfs.utils.doMain

class BsxMarketActivity : BindActivity<ActivityBsxMarketBinding>(), ItemViewClickListener<BsxMarketBean> {

    override val contentLayoutId: Int
        get() = R.layout.activity_bsx_market

    private val adapter = Adapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        initView()
    }

    private fun initView() {
        binding.ivNext.setOnClickListener { navigateTo(BsxHoldingActivity::class.java) }
        binding.rvList.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        Singles.zip(
                App.get().scfApi.getHoldingSum(App.get().passportRepository.currPassport.value!!.address).check(),
                App.get().scfApi.getBsxMarketList().check())
                .doMain()
                .withLoading()
                .subscribeBy(
                        onSuccess = {
                            val first = it.first
                            val second = it.second

                            binding.tvInvestMoney.text = "≈" + if (null == first.corpus) {
                                "0"
                            } else StringUtils.keep4double(first.corpus)
                            binding.tvWaitProfit.text = "≈" + if (null == first.profit) {
                                "0"
                            } else StringUtils.keep4double(first.profit)
                            binding.records = second

                        },
                        onError = {
                            toast(it.message ?: getString(R.string.system_error))
                        })
    }

    override fun onItemClick(item: BsxMarketBean?, position: Int, viewId: Int) {
        startActivity(Intent(this, BsxDetailActivity::class.java)
                .putExtra("assetCode", item!!.assetCode)
                .putExtra("name", item.name)
                .putExtra("titleName", item.saleInfo.name)
                .putExtra("contractAddress", item.contractAddress))
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
