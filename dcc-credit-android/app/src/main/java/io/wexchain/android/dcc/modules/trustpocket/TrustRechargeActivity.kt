package io.wexchain.android.dcc.modules.trustpocket

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.constant.ResultCodes
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.trans.activity.CreateTransactionActivity
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.onSaveImageToGallery
import io.wexchain.android.dcc.vm.setSelfScale
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustRechargeBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal

@SuppressLint("SetTextI18n")
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
        getDepositParam(mCode)

        getHolding(getDigitalCurrencyByCode(mCode))

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
                    else getString(R.string.toast_msg9))
                },
                onSuccess = {
                    toast(getString(R.string.toast_msg11))
                })

        binding.btCopy.setOnClickListener {
            binding.address?.let {
                getClipboardManager().primaryClip = ClipData.newPlainText("passport address", it)
                toast(R.string.copy_succeed)
            }
        }
        binding.tvRecharge.onClick {
            startActivity(Intent(this, CreateTransactionActivity::class.java).apply {
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, getDigitalCurrencyByCode(mCode))
                putExtra("address", binding.tvAddress.text.toString())
            })
            finish()
        }
    }

    private fun getDigitalCurrencyByCode(mCode: String): DigitalCurrency? {
        var digitalCurrency: DigitalCurrency? = null
        if ("DCC" == mCode) {
            digitalCurrency = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.publicEthChain }
        } else if ("ETH" == mCode) {
            digitalCurrency = Currencies.Ethereum
        } else {
            Thread(Runnable {
                val currencyMeta = App.get().assetsRepository.getCurrencyMeta(mCode)
                if (null != currencyMeta) {
                    digitalCurrency = currencyMeta.toDigitalCurrency()
                } else {
                    digitalCurrency = null
                }
            }).start()
        }
        return digitalCurrency
    }

    @SuppressLint("SetTextI18n")
    private fun getHolding(digitalCurrency: DigitalCurrency?) {

        if (null == digitalCurrency) {
            binding.tvRecharge.visibility = View.GONE
            binding.tvBlance.visibility = View.GONE
        } else {
            App.get().assetsRepository.getDigitalCurrencyAgent(digitalCurrency)
                    .getBalanceOf(App.get().passportRepository.getCurrentPassport()!!.address)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.toBigDecimal().subtract("0.001".toBigDecimal()) >= BigDecimal.ZERO) {
                            binding.tvBlance.text = getString(R.string.payment_balance) + digitalCurrency.toDecimalAmount(it).setSelfScale(4) + mCode
                        } else {
                            binding.tvRecharge.visibility = View.GONE
                            binding.tvBlance.visibility = View.GONE
                        }
                    }, {
                    })
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
                    binding.tvAddress.text = it.address
                }, {
                    toast(it.message.toString())
                })
    }

    private fun getDepositParam(code: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getDepositParam(it, code).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.tvMark.text = "最小充值金额：" + it.depositMinAmt + code + "，小于最小金额的充值将不会上账且无法退回。请勿向上述地址充值任何非" + code + "资产，否则资产将不可找回。"

                    binding.tvMark2.text = "您充值至上述地址后，" + (if (null == it.confirmedBlockNumber) "" else it.confirmedBlockNumber) + "次网络确认后到账。"

                }, {
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_RECHARGE_CODE -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    mCode = data!!.getStringExtra("code")
                    mUrl = data!!.getStringExtra("url")

                    getDepositWallet(mCode)
                    getHolding(getDigitalCurrencyByCode(mCode))
                    getDepositParam(mCode)
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
