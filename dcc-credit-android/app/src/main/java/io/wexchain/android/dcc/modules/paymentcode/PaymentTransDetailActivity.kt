package io.wexchain.android.dcc.modules.paymentcode

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
import io.wexchain.dcc.databinding.ActivityPaymentTransDetailBinding
import io.wexchain.ipfs.utils.doMain

@SuppressLint("SetTextI18n")
class PaymentTransDetailActivity : BindActivity<ActivityPaymentTransDetailBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_payment_trans_detail

    private val mId get() = intent.getStringExtra("id")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.ivClose.onClick {
            finish()
        }
        getGoodsOrder(mId)
    }

    private fun getGoodsOrder(id: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getGoodsOrder(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.tvAccount.text = it.amount
                    binding.tvCode.text = it.goods.assetCode
                    binding.tvStatus.text = ViewModelHelper.showPaymentTradeStaus(it.status)
                    binding.tvTitle.text = it.goods.name
                    binding.tvMark.text = it.goods.description
                    binding.tvTime.text = ViewModelHelper.showRedPacketInviteTime(it.lastUpdatedTime)
                    binding.tvId.text = it.id
                    binding.tvUserMark.text = it.payerMemo
                    binding.tvAccount2.text = it.amount + " " + it.goods.assetCode
                    binding.tvFee.text = it.amount.toBigDecimal().subtract(it.receivedAmount.toBigDecimal()).toPlainString() + " " + it.goods.assetCode
                    binding.tvToAccount.text = it.receivedAmount + " " + it.goods.assetCode

                    binding.rlOrderId.onClick {
                        getClipboardManager().primaryClip = ClipData.newPlainText("passport address", it.id)
                        toast(R.string.copy_succeed)
                    }

                }, {
                    toast(it.message.toString())
                })
    }
}
