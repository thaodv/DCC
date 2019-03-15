package io.wexchain.android.dcc.modules.trustpocket

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.constant.ResultCodes
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.addressbook.activity.AddressBookActivity
import io.wexchain.android.dcc.modules.other.QrScannerActivity
import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.dialog.TrustWithdrawDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustWithdrawBinding
import io.wexchain.dccchainservice.domain.trustpocket.WithdrawBean
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal

class TrustWithdrawActivity : BindActivity<ActivityTrustWithdrawBinding>(), TextWatcher {

    override val contentLayoutId: Int get() = R.layout.activity_trust_withdraw

    private lateinit var mCode: String
    private lateinit var mUrl: String

    private lateinit var mMinAccount: String

    private lateinit var mTotalAccount: String
    private lateinit var mFee: String
    private lateinit var mToAccount: String

    private lateinit var trustWithdrawDialog: TrustWithdrawDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        mCode = intent.getStringExtra("code")
        mUrl = intent.getStringExtra("url")

        getAssetConfigMinAmountByCode(mCode)

        getBalance(mCode)

        binding.url = mUrl
        binding.name = mCode

        binding.tvAssetCode.text = mCode

        binding.rlChoose.onClick {
            startActivityForResult(
                    Intent(this, TrustChooseCoinActivity::class.java).putExtra("use", "reWithdraw"),
                    RequestCodes.CHOOSE_WITHDRAW_CODE
            )
        }

        binding.ivScan.onClick {
            startActivityForResult(Intent(this, QrScannerActivity::class.java).apply {
                putExtra(Extras.EXPECTED_SCAN_TYPE, QrScannerActivity.SCAN_TYPE_ADDRESS)
            }, RequestCodes.SCAN)
        }

        binding.ivShoose.onClick {
            startActivityForResult(
                    Intent(this, AddressBookActivity::class.java).putExtra("usage", 1),
                    RequestCodes.CHOOSE_BENEFICIARY_ADDRESS
            )
        }

        binding.tvAll.onClick {
            val address = binding.etAddress.text.trim().toString()
            if ("" == address) {
                toast("提币地址不能为空")
            } else {
                binding.etAccount.setText(mTotalAccount)
            }

        }

        binding.btWithdraw.onClick {
            val address = binding.etAddress.text.trim().toString()
            val account = binding.etAccount.text.trim().toString()
            if ("" == address) {
                toast("提币地址不能为空")
            } else if ("" == account) {
                toast("提币数量不能为空")
            } else if (account.toBigDecimal().subtract(mMinAccount.toBigDecimal()) <= BigDecimal.ZERO) {
                toast("最低" + mMinAccount + "个起提")
            } else if (account.toBigDecimal().subtract(mTotalAccount.toBigDecimal()) > BigDecimal.ZERO) {
                toast("超过可提总量")
            } else {
                trustWithdrawDialog = TrustWithdrawDialog(this)

                trustWithdrawDialog.setParameters(address, account + "" + mCode, "$mFee $mCode", "$mToAccount $mCode", "$mTotalAccount $mCode")

                trustWithdrawDialog.setOnClickListener(object : TrustWithdrawDialog.OnClickListener {
                    override fun sure() {
                        withdraw(address, account)
                    }
                })
                trustWithdrawDialog.show()
            }
        }

        binding.etAccount.addTextChangedListener(this)
    }

    fun withdraw(address: String, account: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.withdraw(it, mCode, account.toBigDecimal(), address).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    if (it.status == WithdrawBean.Status.PROCESSING || it.status == WithdrawBean.Status.PROCESSING) {
                        trustWithdrawDialog.dismiss()
                        navigateTo(TrustWithdrawSuccessActivity::class.java) {
                            putExtra("address", it.receiverAddress)
                            putExtra("account", it.amount.decimalValue + " " + it.assetCode)
                        }
                    } else {
                        toast("系统错误")
                    }
                }, {
                    toast(it.message.toString())
                })

    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = s.toString().trim()
        if ("" != text) {
            getWithdrawFee(mCode, binding.etAddress.text.trim().toString(), text)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getWithdrawFee(code: String, address: String, amount: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getWithdrawFee(it, code, address, amount).check()
                }
                .doMain()
                .subscribe({
                    mFee = it.decimalValue.toBigDecimal().toPlainString()

                    binding.tvFee.text = "手续费$mFee $mCode"

                    mToAccount = amount.toBigDecimal().subtract(it.decimalValue.toBigDecimal()).toPlainString()

                    binding.tvToAccount.text = mToAccount

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
                    mTotalAccount = it.availableAmount.assetValue.amount
                    binding.tvAccount.text = mTotalAccount + " " + it.availableAmount.assetValue.assetCode
                }, {
                    toast(it.message.toString())
                })
    }

    private fun getAssetConfigMinAmountByCode(code: String) {

        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getAssetConfigMinAmountByCode(it, code).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    mMinAccount = it
                    binding.etAccount.hint = "最低" + it + "个起提"
                }, {
                    toast(it.message.toString())
                })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_WITHDRAW_CODE -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    mCode = data!!.getStringExtra("code")
                    mUrl = data!!.getStringExtra("url")

                    binding.url = mUrl
                    binding.name = mCode

                    binding.tvAssetCode.text = mCode

                    getAssetConfigMinAmountByCode(mCode)

                    getBalance(mCode)

                    val account = binding.etAccount.text.trim().toString()

                    if ("" != account) {
                        getWithdrawFee(mCode, binding.etAddress.text.trim().toString(), account)
                    }
                }
            }
            RequestCodes.SCAN -> {
                val address = data?.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                if (address != null) {
                    binding.etAddress.setText(address)
                    binding.executePendingBindings()
                    binding.etAddress.setSelection(address.length)
                }
            }
            RequestCodes.CHOOSE_BENEFICIARY_ADDRESS -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    val ba = data?.getSerializableExtra(Extras.EXTRA_SELECT_ADDRESS) as? AddressBook
                    if (ba != null) {
                        binding.etAddress.setText(ba.address)
                        binding.executePendingBindings()
                        binding.etAddress.setSelection(ba.address.length)
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
