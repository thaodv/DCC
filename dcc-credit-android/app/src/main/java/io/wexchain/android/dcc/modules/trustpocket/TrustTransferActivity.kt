package io.wexchain.android.dcc.modules.trustpocket

import android.content.Intent
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.constant.ResultCodes
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.dialog.TrustTransferDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustTransferBinding
import io.wexchain.dccchainservice.domain.trustpocket.TransferBean
import io.wexchain.digitalwallet.util.isNumberkeep8
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal
import java.math.RoundingMode

class TrustTransferActivity : BindActivity<ActivityTrustTransferBinding>() {

    private val mobileUserId get() = intent.getStringExtra("mobileUserId")
    private val memberId get() = intent.getStringExtra("memberId")
    private val photoUrl get() = intent.getStringExtra("photoUrl")
    private val nickName get() = intent.getStringExtra("nickName")
    private val mobile get() = intent.getStringExtra("mobile")
    private val address get() = intent.getStringExtra("address")

    private lateinit var mCode: String

    private lateinit var mTotalAccount: String

    private lateinit var trustTransferDialog: TrustTransferDialog

    override val contentLayoutId: Int get() = R.layout.activity_trust_transfer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        //mCode = intent.getStringExtra("code")

        binding.rlChoose.onClick {
            startActivityForResult(
                    Intent(this, TrustChooseCoinActivity::class.java).putExtra("use", "reTransfer"),
                    RequestCodes.CHOOSE_TRANSFER_CODE
            )
        }

        binding.url = photoUrl
        binding.nickName = "昵称:\n$nickName"
        binding.mobile = mobile
        binding.address = address

        binding.btTransfer.onClick {
            val code = binding.tvName.text
            val account = binding.etAccount.text.trim().toString()

            if ("请选择" == code) {
                toast("请选择币种")
            } else if ("" == account) {
                toast("请输入转账数量")
            } else if (account.toBigDecimal().subtract(mTotalAccount.toBigDecimal()) > BigDecimal.ZERO) {
                toast("超过可提总量")
            } else {

                if (isNumberkeep8(account)) {
                    trustTransferDialog = TrustTransferDialog(this)
                    trustTransferDialog.setParameters(mobile, address, "$account $mCode", "$mTotalAccount $mCode")

                    trustTransferDialog.setOnClickListener(object : TrustTransferDialog.OnClickListener {
                        override fun transfer() {
                            transfer(account, mobileUserId)
                        }
                    })
                    trustTransferDialog.show()
                } else {
                    toast("最多小数点后面8位")
                }
            }
        }
    }

    fun transfer(amount: String, mobileUserId: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.transfer(it, mCode, amount.toBigDecimal(), mobileUserId).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.status == TransferBean.Status.SUCCESS) {
                        trustTransferDialog.dismiss()
                        navigateTo(TrustTransferSuccessActivity::class.java) {
                            putExtra("mobile", mobile)
                            putExtra("address", address)
                            putExtra("account", it.amount.decimalValue + " " + mCode)
                        }
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    private fun getBalance(code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getBalance(it, code).check()
                }
                .doMain()
                .subscribe({
                    mTotalAccount = it.availableAmount.assetValue.amount.toBigDecimal().setScale(8, RoundingMode.DOWN).toPlainString()
                    binding.tvAccount.text = mTotalAccount + " " + it.availableAmount.assetValue.assetCode
                }, {
                    toast(it.message.toString())
                })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_TRANSFER_CODE -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    mCode = data!!.getStringExtra("code")
                    binding.tvName.text = mCode
                    getBalance(mCode)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
