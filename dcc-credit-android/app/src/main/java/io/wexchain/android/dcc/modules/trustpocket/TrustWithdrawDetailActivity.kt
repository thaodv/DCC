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
import io.wexchain.dcc.databinding.ActivityTrustWithdrawDetailBinding
import io.wexchain.ipfs.utils.doMain

@SuppressLint("SetTextI18n")
class TrustWithdrawDetailActivity : BindActivity<ActivityTrustWithdrawDetailBinding>() {

    private val mId get() = intent.getStringExtra("id")

    override val contentLayoutId: Int get() = R.layout.activity_trust_withdraw_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        title = ""

        getWithdrawOrder(mId)
    }

    private fun getWithdrawOrder(id: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getWithdrawOrder(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.tvAmount.text = "- " + it.amount.decimalValue + it.assetCode
                    binding.tvStatus.text = ViewModelHelper.showTrustWithdrawStatus(it.status)
                    binding.tvOrderId.text = it.requestIdentity.requestNo
                    binding.tvFee.text = it.fee.decimalValue.toBigDecimal().toPlainString() + " " + it.assetCode
                    binding.tvAccount.text = it.amount.decimalValue + " " + it.assetCode
                    binding.tvAddress.text = it.receiverAddress
                    binding.tvTime.text = ViewModelHelper.showRedPacketInviteTime(it.createdTime)

                    binding.tvOrderId.onClick {
                        getClipboardManager().primaryClip = ClipData.newPlainText("passport address", it.requestIdentity.requestNo)
                        toast(R.string.copy_succeed)
                    }

                    binding.tvAddress.onClick {
                        getClipboardManager().primaryClip = ClipData.newPlainText("passport address", it.receiverAddress)
                        toast(R.string.copy_succeed)
                    }

                }, {
                    toast(it.message.toString())
                })

    }

}
