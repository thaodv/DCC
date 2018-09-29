package io.wexchain.android.dcc.modules.bsx

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.tools.StringUtils
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxHoldingBinding
import io.wexchain.dcc.databinding.ItemBsxHoldingBinding
import io.wexchain.dccchainservice.domain.BsxHoldingBean

class BsxHoldingActivity : BindActivity<ActivityBsxHoldingBinding>(), ItemViewClickListener<BsxHoldingBean> {


    override val contentLayoutId: Int
        get() = R.layout.activity_bsx_holding

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
        App.get().scfApi.getHoldingSum(App.get().passportRepository.currPassport.value!!.address)
                .checkonMain()
                .withLoading()
                .subscribe({
                    binding.tvInvestMoney.text = "≈" + if (null == it.corpus) {
                        "0"
                    } else StringUtils.keep4double(it.corpus)
                    binding.tvWaitProfit.text = "≈" + if (null == it.profit) {
                        "0"
                    } else StringUtils.keep4double(it.profit)
                }, {

                })

        App.get().scfApi.getBsxHoldingList(App.get().passportRepository.currPassport.value!!.address)
                .checkonMain()
                .withLoading()
                .subscribe({
                    binding.records = it
                }, {
                    toast(it.message ?: "系统错误")
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
