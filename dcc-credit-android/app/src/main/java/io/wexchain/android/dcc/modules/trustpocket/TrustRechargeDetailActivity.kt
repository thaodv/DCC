package io.wexchain.android.dcc.modules.trustpocket

import android.annotation.SuppressLint
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustRechargeDetailBinding
import io.wexchain.ipfs.utils.doMain

class TrustRechargeDetailActivity : BindActivity<ActivityTrustRechargeDetailBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_recharge_detail

    private val mId get() = intent.getStringExtra("id")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        title = ""

        getDepositOrder(mId)
    }

    @SuppressLint("SetTextI18n")
    private fun getDepositOrder(id: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getDepositOrder(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.tvAmount.text = "+ " + it.amount.decimalValue + it.assetCode
                    binding.tvStatus.text = ViewModelHelper.showTrustRechargeStatus(it.status)
                    binding.tvOrderId.text = it.id
                    binding.tvAddress.text = it.depositWalletAddress
                    binding.tvHash.text = ""
                    binding.tvTime.text = ViewModelHelper.showRedPacketInviteTime(it.createdTime)

                }, {
                    toast(it.message.toString())
                })

    }
}
