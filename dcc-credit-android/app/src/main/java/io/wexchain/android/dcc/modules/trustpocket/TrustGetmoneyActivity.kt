package io.wexchain.android.dcc.modules.trustpocket

import android.content.ClipData
import android.os.Bundle
import androidx.view.toBitmap
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustGetmoneyBinding
import io.wexchain.ipfs.utils.doMain

class TrustGetmoneyActivity : BindActivity<ActivityTrustGetmoneyBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_getmoney

    private lateinit var mCode: String
    private lateinit var mUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        mCode = intent.getStringExtra("code")
        mUrl = intent.getStringExtra("url")


        getDepositWallet(mCode)

        binding.btSave.onClick {
            toast(getString(R.string.payment_share_page_title))
            val ss = binding.ivPassportQr.toBitmap()
            GardenOperations.sharePic(ss) {
                toast(it)
            }

        }

        binding.btCopy.setOnClickListener {
            binding.address?.let {
                getClipboardManager().primaryClip = ClipData.newPlainText("passport address", it)
                toast(R.string.copy_succeed)
            }
        }
    }

    private fun getDepositWallet(code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getDepositWallet(it, code).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.address = it.address
                    binding.name = it.assetCode

                }, {
                    toast(it.message.toString())
                })
    }

}
