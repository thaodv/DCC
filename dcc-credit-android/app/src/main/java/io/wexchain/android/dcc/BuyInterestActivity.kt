package io.wexchain.android.dcc

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.stackTrace
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.JuzixConstants.GAS_LIMIT
import io.wexchain.android.dcc.chain.JuzixConstants.GAS_PRICE
import io.wexchain.android.dcc.tools.*
import io.wexchain.android.dcc.view.dialog.BuyConfirmDialogFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBuyInterestBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.EthsTransactionScratch
import org.web3j.abi.FunctionEncoder
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.methods.request.RawTransaction
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import android.text.Spannable
import android.graphics.Color.parseColor
import android.text.style.ForegroundColorSpan
import android.text.SpannableString



class BuyInterestActivity : BindActivity<ActivityBuyInterestBinding>() {

    override val contentLayoutId: Int = R.layout.activity_buy_interest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setWindowExtended()
        initToolbar()
        binding.etBuyamount.hint="最小认购额度"+MyInterestActivity.MINBUYAMOUNT

        var sp=MyInterestActivity.ONAME+"（剩余额度 "+MyInterestActivity.LASTAM+" DCC）"
        val spannableString = SpannableString(sp)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#ED190F"))
        spannableString.setSpan(colorSpan, sp.length-5-MyInterestActivity.LASTAM.toString().length, sp.length-1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.tvOrdername.text=spannableString
        checkBalance()
        initclick()

    }
    val dccJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }
    val p = App.get().passportRepository.getCurrentPassport()!!

    private fun initclick() {
        binding.tvTotal.setOnClickListener {
            binding.buyamount=""+myBalance
        }
        binding.btnBuy.setOnClickListener {
            if(checkBuy()){
                var amount= BigDecimal(binding.etBuyamount.text.toString())

                BuyConfirmDialogFragment.create(
                    EthsTransactionScratch(dccJuzix,p.address,BintApi.contract,amount,GAS_PRICE.toBigDecimal(),
                        GAS_LIMIT)
                )
                    .show(supportFragmentManager, null)
            }
           // testInvest()
        }
        binding.tvPubtopri.setOnClickListener {
            navigateTo(DccExchangeActivity::class.java)
        }
    }

    private fun checkBuy(): Boolean {
        if (null!=binding.etBuyamount.text.toString()&&!binding.etBuyamount.text.toString().equals("")){


        var a= BigDecimal(binding.etBuyamount.text.toString())
        if(a<MyInterestActivity.MINBUYAMOUNT.toBigDecimal()){
            toast("最低买入"+MyInterestActivity.MINBUYAMOUNT+"DCC")
            return false
        }else if( a>MyInterestActivity.LASTAM.toBigDecimal()){
            toast("最多买入"+MyInterestActivity.LASTAM+"DCC")
            return false
        }else{
            return true
        }
        }else{ toast("请输入购买数量")
            return false

        }
    }

    var agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccJuzix)

    var myBalance=0
    fun checkBalance(){
        agent.getBalanceOf(p.address).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                myBalance=it.toBigDecimal().scaleByPowerOfTen(-18).toBigInteger().toInt()
                binding.tvCanuselable.text="可用额度："+myBalance+" DCC"

            }, {
                stackTrace(it)
            })
    }

    fun testInvest(){
        val approve = Erc20Helper.invest(   BigDecimal("1") .scaleByPowerOfTen(18).toBigInteger() )


        agent.getNonce(p.address) .observeOn(AndroidSchedulers.mainThread()).subscribe(
            {
                val rawTransaction = RawTransaction.createTransaction(
                    it,
                    GAS_PRICE,
                    GAS_LIMIT,
                    BintApi.contract,
                    BigInteger.ZERO,
                    FunctionEncoder.encode(approve)
                )
                val signed = Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, App.get().passportRepository.getCurrentPassport()!!.credential))
                val txHash = App.get().bintApi.sendRawTransaction(signed)
                    .blockingGet()
                Log.e("txHashtxHash",txHash)
                println("sent approve tx")
                val response = App.get().bintApi.transactionReceipt(txHash)
                 //    .retryWhen(RetryWithDelay.createGrowth(8, 1000))
                    .blockingGet()
                println("receipt got: approve tx")
                Log.e("responseresponse",response.toString())

            }, { stackTrace(it) }
        )

    }



}
