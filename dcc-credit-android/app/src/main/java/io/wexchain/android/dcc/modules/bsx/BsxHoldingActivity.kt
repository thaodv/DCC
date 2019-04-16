package io.wexchain.android.dcc.modules.bsx

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.StringUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxHoldingBinding
import io.wexchain.dcc.databinding.ItemBsxHoldingBinding
import io.wexchain.dccchainservice.domain.BsxHoldingBean
import io.wexchain.ipfs.utils.doMain

class BsxHoldingActivity : BindActivity<ActivityBsxHoldingBinding>(), ItemViewClickListener<BsxHoldingBean> {

    override val contentLayoutId: Int get() = R.layout.activity_bsx_holding

    private val adapter = Adapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        binding.tvBuyAgain.setOnClickListener { finish() }
        binding.rvList.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        Singles.zip(
                App.get().scfApi.getHoldingSum(App.get().passportRepository.currPassport.value!!.address).check(),
                App.get().scfApi.getBsxHoldingList(App.get().passportRepository.currPassport.value!!.address).check())
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

    override fun onItemClick(item: BsxHoldingBean?, position: Int, viewId: Int) {
        startActivity(Intent(this, BsxHoldingDetailActivity::class.java)
                .putExtra("bsxHoldingBean", item))
    }

    private class Adapter(itemViewClickListener: ItemViewClickListener<BsxHoldingBean>) :
            DataBindAdapter<ItemBsxHoldingBinding, BsxHoldingBean>(
                    layout = R.layout.item_bsx_holding,
                    itemDiffCallback = defaultItemDiffCallback(),
                    itemViewClickListener = itemViewClickListener
            ) {
        override fun bindData(binding: ItemBsxHoldingBinding, item: BsxHoldingBean?) {
            binding.bsxHoldingBean = item
        }

    }


}
