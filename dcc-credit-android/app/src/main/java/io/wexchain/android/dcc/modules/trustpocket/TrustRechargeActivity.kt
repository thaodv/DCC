package io.wexchain.android.dcc.modules.trustpocket

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.constant.ResultCodes
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.onSaveImageToGallery
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustRechargeBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.ipfs.utils.doMain

class TrustRechargeActivity : BindActivity<ActivityTrustRechargeBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_trust_recharge

    private lateinit var mCode: String
    private lateinit var mUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        mCode = intent.getStringExtra("code")
        mUrl = intent.getStringExtra("url")

        getDepositWallet(mCode)

        binding.url = mUrl
        binding.name = mCode

        binding.rlChoose.onClick {
            startActivityForResult(
                    Intent(this, TrustChooseCoinActivity::class.java).putExtra("use", "reRecharge"),
                    RequestCodes.CHOOSE_RECHARGE_CODE
            )
        }

        binding.btSave.onSaveImageToGallery(binding.ivPassportQr,
                onError = {
                    toast(if (it is DccChainServiceException)
                        it.message!!
                    else "保存失败")
                },
                onSuccess = {
                    toast("已保存至相册")
                })

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


                }, {
                    toast(it.message.toString())
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_RECHARGE_CODE -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    mCode = data!!.getStringExtra("code")
                    mUrl = data!!.getStringExtra("url")

                    getDepositWallet(mCode)
                    binding.url = mUrl
                    binding.name = mCode
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_trust_recharge, menu)


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.recharge_record -> {
                navigateTo(TrustRechargeRecordsActivity::class.java) {
                    putExtra("code", mCode)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
