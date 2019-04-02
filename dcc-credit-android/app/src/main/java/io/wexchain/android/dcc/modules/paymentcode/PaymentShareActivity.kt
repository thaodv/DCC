package io.wexchain.android.dcc.modules.paymentcode

import android.annotation.SuppressLint
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.common.tools.CommonUtils
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.onSaveImageToGallery
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentShareBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.ipfs.utils.doMain
import java.math.RoundingMode

class PaymentShareActivity : BindActivity<ActivityPaymentShareBinding>() {

    private val mId get() = intent.getStringExtra("id")
    private val mCode get() = intent.getStringExtra("code")

    override val contentLayoutId: Int get() = R.layout.activity_payment_share

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.ivClose.onClick {
            finish()
        }

        val res = if (CommonUtils.isRMB()) "CNY" else "USDT"
        "$mCode-$res"

        quote("$mCode-$res")

        binding.ivSave.onSaveImageToGallery(binding.rlContent,
                onError = {
                    toast(if (it is DccChainServiceException)
                        it.message!!
                    else "保存失败")
                },
                onSuccess = {
                    toast("保存成功")
                })
    }

    private fun quote(pair: String) {
        App.get().marketingApi.quote(pair).check()
                .doMain()
                .withLoading()
                .subscribe({
                    getGoods(mId, it)
                }, {

                })
    }

    @SuppressLint("SetTextI18n")
    private fun getGoods(id: String, rate: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getGoods(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.tvTitle.text = it.name
                    binding.tvDescription.text = it.description

                    if (null != it.amount) {
                        binding.tvAccount.text = CommonUtils.showCurrencySymbol() + " " + it.amount!!.toBigDecimal().multiply(rate.toBigDecimal()).setScale(4, RoundingMode.DOWN).toPlainString() + " " + mCode
                    }

                    binding.tvUser.text = it.mobile + resources.getString(R.string.payment_share_text1)
                    binding.address = it.url

                }, {
                    toast(it.message.toString())
                })
    }

}
