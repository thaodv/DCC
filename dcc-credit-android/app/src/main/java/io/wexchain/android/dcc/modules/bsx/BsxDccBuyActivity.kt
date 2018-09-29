package io.wexchain.android.dcc.modules.bsx

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.stackTrace
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.DccExchangeActivity
import io.wexchain.android.dcc.chain.JuzixConstants
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.view.dialog.BsxDccBuyConfirmDialogFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxDccBuyBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.EthsTransactionScratch
import java.math.BigDecimal

class BsxDccBuyActivity : BindActivity<ActivityBsxDccBuyBinding>() {

    private val contractAddress get() = intent.getStringExtra("contractAddress")

    override val contentLayoutId: Int = R.layout.activity_bsx_dcc_buy

    val p = App.get().passportRepository.getCurrentPassport()!!
    val dccJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }

    var myBalance = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        toolbarTitle!!.text = "认购额度"
        binding.etBuyamount.hint = "最小认购额度" + BsxDetailActivity.MINBUYAMOUNT

        var sp = BsxDetailActivity.ONAME + "（剩余额度 " + BsxDetailActivity.LASTAM + " DCC）"
        val spannableString = SpannableString(sp)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#ED190F"))
        spannableString.setSpan(colorSpan, sp.length - 5 - BsxDetailActivity.LASTAM.length, sp.length - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
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

                BsxDccBuyConfirmDialogFragment.create(
                        EthsTransactionScratch(dccJuzix, p.address, contractAddress, amount, JuzixConstants.GAS_PRICE.toBigDecimal(),
                                JuzixConstants.GAS_LIMIT)
                ).show(supportFragmentManager, null)
            }
        }
        binding.tvPubtopri.setOnClickListener {
            navigateTo(DccExchangeActivity::class.java)
        }
    }

    private fun checkBuy(): Boolean {

        if (binding.etBuyamount.text.toString() != "") {

            var a = BigDecimal(binding.etBuyamount.text.toString())
            if (a < BsxDetailActivity.MINBUYAMOUNT.toBigDecimal()) {
                toast("最低买入" + BsxDetailActivity.MINBUYAMOUNT + "DCC")
                return false
            } else if (a > BsxDetailActivity.LASTAM.toBigDecimal()) {
                toast("最多买入" + BsxDetailActivity.LASTAM + "DCC")
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

        var agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccJuzix)
        agent.getBalanceOf(p.address).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    myBalance = it.toBigDecimal().scaleByPowerOfTen(-18).toBigInteger().toInt()
                    binding.tvCanuselable.text = "可用额度：$myBalance DCC"

                }, {
                    stackTrace(it)
                })
    }

}