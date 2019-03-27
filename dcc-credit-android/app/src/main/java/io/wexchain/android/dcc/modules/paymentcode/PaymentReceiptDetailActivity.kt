package io.wexchain.android.dcc.modules.paymentcode

import android.annotation.SuppressLint
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentReceiptDetailBinding
import io.wexchain.ipfs.utils.doMain

class PaymentReceiptDetailActivity : BindActivity<ActivityPaymentReceiptDetailBinding>() {

    private val mId get() = intent.getStringExtra("id")

    override val contentLayoutId: Int get() = R.layout.activity_payment_receipt_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.ivClose.onClick {
            finish()
        }

        binding.ivDelete.onClick {
            closeGoods(mId)
        }


    }

    @SuppressLint("SetTextI18n")
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
