package io.wexchain.android.dcc.modules.paymentcode

import android.content.ClipData
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.onSaveImageToGallery
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentShareBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.ipfs.utils.doMain

class PaymentShareActivity : BindActivity<ActivityPaymentShareBinding>() {

    private val mId get() = intent.getStringExtra("id")
    private val mCode get() = intent.getStringExtra("code")

    override val contentLayoutId: Int get() = R.layout.activity_payment_share

    lateinit var mUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.ivClose.onClick {
            finish()
        }

        getGoods(mId)

        binding.ivShare.onSaveImageToGallery(binding.rlContent,
                onError = {
                    toast(if (it is DccChainServiceException)
                        it.message!!
                    else "保存失败")
                },
                onSuccess = {
                    toast("保存成功")
                })

        binding.ivSave.onClick {
            binding.address?.let {
                getClipboardManager().primaryClip = ClipData.newPlainText("passport address", it)
                toast(R.string.copy_succeed)
            }
        }
    }

    private fun getGoods(id: String) {
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
                        binding.tvAccount.text = it.amount!! + " " + mCode
                    }

                    binding.tvUser.text = it.mobile + resources.getString(R.string.payment_share_text1)
                    mUrl = it.url
                    binding.address = mUrl


                }, {
                    toast(it.message.toString())
                })
    }

}
