package io.wexchain.android.dcc.modules.bsx

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.alibaba.fastjson.JSON
import io.reactivex.Single
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.BsxOperations
import io.wexchain.android.dcc.domain.SaleInfo
import io.wexchain.android.dcc.network.IpfsApi
import io.wexchain.android.dcc.tools.BytesUtils
import io.wexchain.android.dcc.tools.BytesUtils.encodeStringsimple
import io.wexchain.android.dcc.tools.BytesUtils.encodeStringsimple2
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxDetailBinding
import io.wexchain.digitalwallet.api.domain.EthJsonRpcResponse
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal

class BsxDetailActivity : BindActivity<ActivityBsxDetailBinding>() {

    companion object {
        var MINBUYAMOUNT = 0.0
        var LASTAM = 0.0
        var ONAME = "私链DCC币生息1期"
    }

    private val assetCode get() = intent.getStringExtra("assetCode")
    private val name get() = intent.getStringExtra("name")
    private val contractAddress get() = intent.getStringExtra("contractAddress")

    override val contentLayoutId: Int = R.layout.activity_bsx_detail

    lateinit var bussiness: String

    lateinit var saleInfo: SaleInfo

    private var totalAmount: Int = 0
    private var lastAmount: Double = 0.0
    private var minAmountPerHand: Double = 0.0

    var statu = "已结束"

    var canBuy: Boolean = false

    lateinit var sstvBuyit: ColorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        binding.realnamecert.text = assetCode
        binding.canbuy = canBuy
        sstvBuyit = binding.btBuy.background as ColorDrawable


        if ("DCC" == assetCode) {
            if ("1" == name) {
                bussiness = IpfsApi.BSX_DCC_01
            } else if ("2" == name) {
                bussiness = IpfsApi.BSX_DCC_02
            }
        } /*else if ("ETH" == assetCode) {
            bussiness = ""
        }*/

        binding.btBuy.setOnClickListener {
            if (canBuy) {

                if ("DCC" == assetCode) {
                    startActivity(Intent(this, BsxDccBuyActivity::class.java)
                            .putExtra("contractAddress", contractAddress))
                } else {
                    startActivity(Intent(this, BsxEthBuyActivity::class.java)
                            .putExtra("contractAddress", contractAddress))
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()

        getBsxSaleInfo()

    }

    private fun getBsxSaleInfo() {
        var ss: Single<EthJsonRpcResponse<String>>

        if ("DCC" == assetCode) {
            ss = BsxOperations.getBsxSaleInfo(bussiness)
        } else {
            ss = App.get().publicRpc.getBsxSaleInfo(contractAddress)
        }

        ss.doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }.subscribe({
                    var ss = BytesUtils.encodeString(it.result)//.replace(" ","")
                    saleInfo = JSON.parseObject(ss, SaleInfo::class.java)
                    binding.saleInfo = saleInfo
                    getBsxMinAmountPerHando()
                }, {

                })
    }

    private fun getBsxMinAmountPerHando() {

        var ss: Single<EthJsonRpcResponse<String>>

        if ("DCC" == assetCode) {
            ss = BsxOperations.getBsxMinAmountPerHand(bussiness)
        } else {
            ss = App.get().publicRpc.getBsxMinAmountPerHand(contractAddress)
        }

        ss.doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }.subscribe({

                    if ("DCC" == assetCode) {
                        minAmountPerHand = BytesUtils.encodeStringsimple(it.result).toDouble()
                    } else {
                        minAmountPerHand = BytesUtils.encodeStringsimple2(it.result)
                    }

                    MINBUYAMOUNT = minAmountPerHand
                    binding.minAmountPerHandDCC = minAmountPerHand.toString() + assetCode
                    getBsxInvestCeilAmount()
                }, {

                })
    }

    private fun getBsxInvestCeilAmount() {

        var ss: Single<EthJsonRpcResponse<String>>

        if ("DCC" == assetCode) {
            ss = BsxOperations.getBsxInvestCeilAmount(bussiness)
        } else {
            ss = App.get().publicRpc.getBsxInvestCeilAmount(contractAddress)
        }

        ss.doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }.subscribe({
                    totalAmount = encodeStringsimple(it.result)
                    binding.totalamountDCC = ("" + totalAmount + assetCode)
                    getbiInvestedTotalAmount()
                }, {
                    it.printStackTrace()
                })
    }

    private fun getbiInvestedTotalAmount() {

        var ss: Single<EthJsonRpcResponse<String>>

        if ("DCC" == assetCode) {
            ss = BsxOperations.investedBsxTotalAmount(bussiness)
        } else {
            ss = App.get().publicRpc.investedBsxTotalAmount(contractAddress)
        }

        ss.doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }.subscribe({
                    if ("DCC" == assetCode) {
                        lastAmount = encodeStringsimple(it.result).toDouble()
                    } else {
                        lastAmount = encodeStringsimple2(it.result)
                    }

                    var ssss = BigDecimal((totalAmount - lastAmount) * 100).divide(BigDecimal(totalAmount), 1, BigDecimal.ROUND_HALF_UP).setScale(1)
                    binding.lastamountDCC = ("" + (totalAmount - lastAmount) + assetCode + " (" + ssss + "%）")
                    getBsxStatus()
                }, {
                    it.printStackTrace()
                })
    }

    private fun getBsxStatus() {

        var ss: Single<EthJsonRpcResponse<String>>

        if ("DCC" == assetCode) {
            ss = BsxOperations.getBsxStatus(bussiness)
        } else {
            ss = App.get().publicRpc.getBsxStatus(contractAddress)
        }

        ss.doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }.subscribe({
                    var mystatu = BytesUtils.encodeStringstatu(it.result)

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
                }, {

                })
    }

    private fun setButton() {
        binding.btBuy.text = statu

        if (canBuy) {
            sstvBuyit.color = Color.parseColor("#FF6766CC")
        } else {
            sstvBuyit.color = Color.parseColor("#B2484848")
        }
        binding.btBuy.background = sstvBuyit
    }


}
