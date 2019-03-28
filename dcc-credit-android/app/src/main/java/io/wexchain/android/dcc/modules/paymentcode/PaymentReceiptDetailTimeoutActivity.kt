package io.wexchain.android.dcc.modules.paymentcode

import android.annotation.SuppressLint
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentReceiptDetailTimeoutBinding
import io.wexchain.ipfs.utils.doMain

class PaymentReceiptDetailTimeoutActivity : BindActivity<ActivityPaymentReceiptDetailTimeoutBinding>() {

    private val mId get() = intent.getStringExtra("id")

    override val contentLayoutId: Int get() = R.layout.activity_payment_receipt_detail_timeout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getGoodsView(mId)

        binding.ivClose.onClick {
            finish()
        }

        binding.tvTransRecords.onClick {
            navigateTo(PaymentTransRecordsActivity::class.java) {
                putExtra("goodsId", mId)
            }
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
                    binding.tvNum.text = it.totalStats.orderNumber
                    binding.tvAccount.text = it.totalStats.orderAmount

                    binding.tvCode.text = it.goods.assetCode
                    binding.tvCode2.text = it.goods.assetCode


                    binding.tvTitle.text = it.goods.name
                    binding.tvDescription.text = it.goods.description
                    binding.tvAccount2.text = it.totalStats.orderAmount + " " + it.goods.assetCode

                    binding.tvStatus.text = getString(R.string.payment_receipt_detail_status_closed)

                    if (null == it.goods.expiredTime) {
                        binding.tvDeadtime.text = getString(R.string.payment_add_deadtime_v)
                    } else {
                        binding.tvDeadtime.text = ViewModelHelper.showRedPacketInviteTime(it.goods.expiredTime!!)
                    }

                }, {
                    toast(it.message.toString())
                })
    }

}
