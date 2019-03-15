package io.wexchain.android.dcc.modules.trustpocket

import android.annotation.SuppressLint
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustTransferDetailBinding
import io.wexchain.ipfs.utils.doMain

class TrustTransferDetailActivity : BindActivity<ActivityTrustTransferDetailBinding>() {

    private val mId get() = intent.getStringExtra("id")

    override val contentLayoutId: Int get() = R.layout.activity_trust_transfer_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        getTransferOrder(mId)
    }

    @SuppressLint("SetTextI18n")
    private fun getTransferOrder(id: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getTransferOrder(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.tvAmount.text = "+ "
                    binding.tvStatus.text = ""
                    binding.tvOrderId.text = ""
                    binding.tvAccount.text = ""
                    binding.tvTime.text = ""

                }, {
                    toast(it.message.toString())
                })

    }

}
