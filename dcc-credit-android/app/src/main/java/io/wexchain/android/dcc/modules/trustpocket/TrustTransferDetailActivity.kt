package io.wexchain.android.dcc.modules.trustpocket

import android.annotation.SuppressLint
import android.content.ClipData
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustTransferDetailBinding
import io.wexchain.ipfs.utils.doMain

@SuppressLint("SetTextI18n")
class TrustTransferDetailActivity : BindActivity<ActivityTrustTransferDetailBinding>() {

    private val mId get() = intent.getStringExtra("id")
    private val mType get() = intent.getStringExtra("type")

    lateinit var tag: String

    override val contentLayoutId: Int get() = R.layout.activity_trust_transfer_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        title = ""

        getTransferOrder(mId)

        if ("1" == mType) {
            tag = "+ "
        } else {
            tag = "- "
        }

    }

    private fun getTransferOrder(id: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getTransferOrder(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.tvAmount.text = tag + it.amount.decimalValue + it.assetCode
                    binding.tvStatus.text = ViewModelHelper.showTrustTransferStatus(it.status)
                    binding.tvOrderId.text = mId
                    binding.tvAccount.text = it.mobile
                    binding.tvTime.text = ViewModelHelper.showRedPacketInviteTime(it.createdTime)

                    binding.tvOrderId.onClick {
                        getClipboardManager().primaryClip = ClipData.newPlainText("passport address", mId)
                        toast(R.string.copy_succeed)
                    }

                }, {
                    toast(it.message.toString())
                })

    }

}
