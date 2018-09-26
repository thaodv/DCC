package io.wexchain.android.dcc.modules.bsx

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.stackTrace
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.JuzixConstants
import io.wexchain.android.dcc.view.dialog.BsxEthBuyConfirmDialogFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxEthBuyBinding
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.EthsTransactionScratch
import java.math.BigDecimal
import java.math.RoundingMode

class BsxEthBuyActivity : BindActivity<ActivityBsxEthBuyBinding>() {

    private val contractAddress get() = intent.getStringExtra("contractAddress")


    val p = App.get().passportRepository.getCurrentPassport()!!

    var myBalance = 0.0

    override val contentLayoutId: Int
        get() = R.layout.activity_bsx_eth_buy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        binding.etBuyamount.hint = "最小认购额度" + BsxDetailActivity.MINBUYAMOUNT

        var sp = BsxDetailActivity.ONAME + "（剩余额度 " + BsxDetailActivity.LASTAM + " ETH）"
        val spannableString = SpannableString(sp)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#ED190F"))
        spannableString.setSpan(colorSpan, sp.length - 5 - BsxDetailActivity.LASTAM.toString().length, sp.length - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.tvOrdername.text = spannableString


        checkBalance()
        initclick()

    }

    private fun initclick() {
        binding.tvTotal.setOnClickListener {
            binding.buyamount = "" + myBalance
        }
        binding.btnBuy.setOnClickListener {
            if (checkBuy()) {
                var amount = BigDecimal(binding.etBuyamount.text.toString())

                BsxEthBuyConfirmDialogFragment.create(
                        EthsTransactionScratch(Currencies.Ethereum, p.address, contractAddress, amount, JuzixConstants.GAS_PRICE.toBigDecimal(),
                                JuzixConstants.GAS_LIMIT)
                ).show(supportFragmentManager, null)
            }
        }
    }

    private fun checkBuy(): Boolean {

        if (binding.etBuyamount.text.toString() != "") {

            var a = BigDecimal(binding.etBuyamount.text.toString())
            if (a < BsxDetailActivity.MINBUYAMOUNT.toBigDecimal()) {
                toast("最低买入" + BsxDetailActivity.MINBUYAMOUNT + "ETH")
                return false
            } else if (a > BsxDetailActivity.LASTAM.toBigDecimal()) {
                toast("最多买入" + BsxDetailActivity.LASTAM + "ETH")
                return false
            } else {
                return true
            }
        } else {
            toast("请输入购买数量")
            return false
        }
    }

    private fun checkBalance() {

        var agent = App.get().assetsRepository.getDigitalCurrencyAgent(Currencies.Ethereum)
        agent.getBalanceOf(p.address).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    myBalance = it.toBigDecimal().scaleByPowerOfTen(-18).setScale(4, RoundingMode.DOWN).toDouble()

                    binding.tvCanuselable.text = "可用额度：" + it.toBigDecimal().scaleByPowerOfTen(-18).setScale(4, RoundingMode.DOWN).toPlainString() + " ETH"

                }, {
                    stackTrace(it)
                })
    }

}
