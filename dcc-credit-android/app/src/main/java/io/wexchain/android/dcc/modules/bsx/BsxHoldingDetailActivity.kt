package io.wexchain.android.dcc.modules.bsx

import android.content.Intent
import android.os.Bundle
import android.view.View
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxHoldingDetailBinding
import io.wexchain.dccchainservice.domain.BsxHoldingBean

class BsxHoldingDetailActivity : BindActivity<ActivityBsxHoldingDetailBinding>() {

    private val bsxHoldingBean get() = intent.getSerializableExtra("bsxHoldingBean") as BsxHoldingBean

    override val contentLayoutId: Int
        get() = R.layout.activity_bsx_holding_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        binding.tvIdtitle.text = "在投本金(" + bsxHoldingBean.assetCode + ")"
        binding.tvCapital.text = bsxHoldingBean.positionAmount
        binding.tvProfit.text = bsxHoldingBean.profit
        binding.tvPendding.text = bsxHoldingBean.expectedRepay
        binding.tvOrdername.text = bsxHoldingBean.saleInfo.name
        binding.tvExprofit.text = bsxHoldingBean.saleInfo.annualRate + "%"
        binding.tvDuration.text = bsxHoldingBean.saleInfo.period + "天"
        binding.tvProfitstatu.text = bsxHoldingBean.saleInfo.profitMethod
        binding.tvStartprotime.text = bsxHoldingBean.saleInfo.showIncomeTime()
        binding.tvEndtime.text = bsxHoldingBean.saleInfo.showEndTime()


        if ("4" == bsxHoldingBean.status) {
            binding.tvRaisedata.visibility = View.GONE
            binding.tvRaisedata2.visibility = View.VISIBLE

            binding.tvLableProfit.text = "收益（" + bsxHoldingBean.assetCode + ")"
            binding.tvLablePendding.text = "本息合计（" + bsxHoldingBean.assetCode + ")"

        } else {
            binding.tvRaisedata.visibility = View.VISIBLE
            binding.tvRaisedata2.visibility = View.GONE

            binding.tvLableProfit.text = "待收收益（" + bsxHoldingBean.assetCode + ")"
            binding.tvLablePendding.text = "待收本息合计（" + bsxHoldingBean.assetCode + ")"

            var statusText = ""

            if ("1" == bsxHoldingBean.status) {
                statusText = "认购中"
            } else if ("2" == bsxHoldingBean.status) {
                statusText = "收益中"
            } else if ("3" == bsxHoldingBean.status) {
                statusText = "已售罄"
            }
            binding.tvRaisedata.text = statusText
        }

        binding.rlDetail.setOnClickListener {
            startActivity(Intent(this, BsxDetailActivity::class.java)
                    .putExtra("assetCode", bsxHoldingBean!!.assetCode)
                    .putExtra("name", bsxHoldingBean!!.name)
                    .putExtra("contractAddress", bsxHoldingBean!!.contractAddress))
        }

    }
}
