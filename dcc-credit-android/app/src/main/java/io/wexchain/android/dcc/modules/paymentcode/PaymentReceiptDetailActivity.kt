package io.wexchain.android.dcc.modules.paymentcode

import android.annotation.SuppressLint
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentReceiptDetailBinding
import io.wexchain.ipfs.utils.doMain

class PaymentReceiptDetailActivity : BindActivity<ActivityPaymentReceiptDetailBinding>() {

    private val mId get() = intent.getStringExtra("id")

    override val contentLayoutId: Int get() = R.layout.activity_payment_receipt_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getGoodsView(mId)

        binding.ivClose.onClick {
            finish()
        }

        binding.ivDelete.onClick {
            closeGoods(mId)
        }


    }

    @SuppressLint("SetTextI18n")
    private fun getGoodsView(id: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getGoodsView(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.tvNum.text = it.todayStats.orderNumber
                    binding.tvAccount.text = it.todayStats.orderAmount

                    binding.tvNum2.text = it.totalStats.orderNumber
                    binding.tvAccount2.text = it.totalStats.orderAmount

                    binding.tvCode.text = it.goods.assetCode
                    binding.tvCode2.text = it.goods.assetCode

                    binding.tvTitle.text = it.goods.name
                    binding.tvDescription.text = it.goods.description
                    binding.tvCode3.text = it.goods.assetCode
                    binding.tvAccount3.text = it.goods.amount
                    binding.tvDeadtime.text = ViewModelHelper.showRedPacketInviteTime(it.goods.expiredTime)


                }, {
                    toast(it.message.toString())
                })
    }

    private fun closeGoods(id: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.closeGoods(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    finish()
                }, {
                    toast(it.message.toString())
                })
    }
}
