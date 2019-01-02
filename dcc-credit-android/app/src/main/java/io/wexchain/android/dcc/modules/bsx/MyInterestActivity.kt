package io.wexchain.android.dcc.modules.bsx

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.domain.SaleInfo
import io.wexchain.android.dcc.tools.BintApi
import io.wexchain.android.dcc.tools.BytesUtils
import io.wexchain.android.dcc.tools.BytesUtils.encodeStringsimple
import io.wexchain.android.dcc.tools.toBean
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyInterestBinding
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.api.domain.EthJsonRpcRequestBody
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal


class MyInterestActivity : BindActivity<ActivityMyInterestBinding>() {

    companion object {
        var MINBUYAMOUNT = 0
        var LASTAM = 0
        var ONAME = "私链DCC币生息1期"
    }

    lateinit var saleInfo: SaleInfo

    var canBuy: Boolean = false

    var totalAmount: Int = 0
    var lastAmount: Int = 0
    var minAmountPerHand: Int = 0

    var statu = "已结束"

    override val contentLayoutId: Int = R.layout.activity_my_interest
    lateinit var sstvBuyit: ColorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initclick()

        binding.canbuy = canBuy
        sstvBuyit = binding.tvBuyit.background as ColorDrawable
    }

    override fun onResume() {
        super.onResume()
        getbiSaleInfo()
    }

    private fun initclick() {
        binding.tvback.setOnClickListener {
            finish()
        }
        binding.tvHave.setOnClickListener {
            navigateTo(MyInterestDetailActivity::class.java)
        }
        binding.tvBuyit.setOnClickListener {
            if (canBuy) {
                navigateTo(BuyInterestActivity::class.java)
            } else {

            }
        }
    }

    private fun getbiSaleInfo() {
        val getAllowance = Erc20Helper.getBsxSaleInfo(BintApi.contract)
        App.get().bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).subscribeOn(AndroidSchedulers.mainThread()).doOnSubscribe {
            showLoadingDialog()
        }.doFinally { hideLoadingDialog() }.subscribe(
                {
                    var ss = BytesUtils.encodeString(it.result)//.replace(" ","")
                    saleInfo = ss.toBean(SaleInfo::class.java)

                    binding.saleInfo = saleInfo
                    //binding.tvProfit.setText(saleInfo.annualRateP())
               //     Log.e("getbiSaleInfo", saleInfo.name)
                    getbiminAmountPerHand()
                },
                {
                    it.printStackTrace()
                }
        )
     //   Log.e("getbiSaleInfo", "getbiSaleInfo")

    }

    private fun getbiminAmountPerHand() {
        val getAllowance = Erc20Helper.getBsxMinAmountPerHand(BintApi.contract)
        App.get().bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).subscribeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    minAmountPerHand = encodeStringsimple(it.result)
                    MINBUYAMOUNT = minAmountPerHand
                    //  saleInfo.minAmountPerHand=minAmountPerHand
                    binding.minAmountPerHandDCC = ("" + minAmountPerHand + " DCC")
              //      Log.e("encodeStringsimple", "" + encodeStringsimple(it.result))
                    getbiInvestCeilAmount()
                },
                {
                    it.printStackTrace()
                }
        )
    }

    private fun getbiInvestCeilAmount() {
        val getAllowance = Erc20Helper.getBsxInvestCeilAmount(BintApi.contract)
        App.get().bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).doMain().subscribe(
                {
                    totalAmount = encodeStringsimple(it.result)
                    binding.totalamountDCC = ("" + totalAmount + " DCC")
                //    Log.e("totalamountDCC", "" + encodeStringsimple(it.result))
                    getbiInvestedTotalAmount()

                },
                {
                    it.printStackTrace()
                }
        )
    }

    private fun getbiInvestedTotalAmount() {
        val getAllowance = Erc20Helper.investedBsxTotalAmount(BintApi.contract)
        App.get().bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).subscribeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    lastAmount = encodeStringsimple(it.result)
                    var ssss = BigDecimal((totalAmount - lastAmount) * 100).divide(BigDecimal(totalAmount), 1, BigDecimal.ROUND_HALF_UP).setScale(1)
                    binding.lastamountDCC = ("" + (totalAmount - lastAmount) + " DCC (" + ssss + "%）")
                    checkStatu()
                },
                {
                    it.printStackTrace()
                }
        )
    }

    private fun checkStatu() {
        require(null != saleInfo)

        val getAllowance = Erc20Helper.getBsxStatus(BintApi.contract)
        App.get().bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).doMain().subscribe(
                {
                    var mystatu = BytesUtils.encodeStringstatu(it.result)

                 //   Log.e("getbiStatues", "" + mystatu)
                    if (mystatu != 1) {
                        statu = "已结束"
                        canBuy = false
                    } else if (minAmountPerHand > (totalAmount - lastAmount)) {
                        statu = "已售罄"
                        canBuy = false
                        //   binding.tvBuyit.setBackgroundResource(R.color.B2484848)
                    } else {
                        statu = "认购"
                        canBuy = true
                        // binding.tvBuyit.setBackgroundResource(R.color.FF6766CC)
                    }
                    LASTAM = totalAmount - lastAmount
                    ONAME = saleInfo.name
                    runOnMainThread {
                        setButton()
                    }

                },
                {
                    it.printStackTrace()
                }
        )
    }

    private fun setButton() {
        binding.tvBuyit.text = statu

        if (canBuy) {
            sstvBuyit.color = Color.parseColor("#FF6766CC")
        } else {
            sstvBuyit.color = Color.parseColor("#B2484848")
        }
        binding.tvBuyit.background = sstvBuyit
    }
}
