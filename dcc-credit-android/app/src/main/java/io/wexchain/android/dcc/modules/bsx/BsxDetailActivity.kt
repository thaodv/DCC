package io.wexchain.android.dcc.modules.bsx

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import io.reactivex.Single
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.runOnMainThread
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.BsxOperations
import io.wexchain.android.dcc.domain.SaleInfo
import io.wexchain.android.dcc.network.IpfsApi
import io.wexchain.android.dcc.tools.BytesUtils
import io.wexchain.android.dcc.tools.BytesUtils.encodeStringsimple
import io.wexchain.android.dcc.tools.BytesUtils.encodeStringsimple2
import io.wexchain.android.dcc.tools.LogUtils
import io.wexchain.android.dcc.tools.toBean
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxDetailBinding
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.api.domain.EthJsonRpcResponse
import io.wexchain.ipfs.utils.doMain
import io.wexchain.ipfs.utils.subscribeOnIo
import java.math.BigDecimal

class BsxDetailActivity : BindActivity<ActivityBsxDetailBinding>() {

    companion object {
        var MINBUYAMOUNT: String = ""
        var LASTAM: String = ""
        var ONAME = ""
    }

    private val assetCode get() = intent.getStringExtra("assetCode")
    private val name get() = intent.getStringExtra("name")
    private val titleName get() = intent.getStringExtra("titleName")
    private val contractAddress get() = intent.getStringExtra("contractAddress")

    override val contentLayoutId: Int = R.layout.activity_bsx_detail

    lateinit var bussiness: String

    lateinit var saleInfo: SaleInfo

    private var totalAmount: String = ""
    private var lastAmount: String = ""
    private var minAmountPerHand: String = ""

    var statu = "已结束"

    var canBuy: Boolean = false

    lateinit var sstvBuyit: ColorDrawable

    val agent = App.get().assetsRepository.getDigitalCurrencyAgent(Currencies.Ethereum)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        title = titleName
        binding.realnamecert.text = assetCode
        binding.canbuy = canBuy
        sstvBuyit = binding.btBuy.background as ColorDrawable


        if ("DCC" == assetCode) {
            if ("1" == name) {
                bussiness = IpfsApi.BSX_DCC_01
            } else if ("2" == name) {
                bussiness = IpfsApi.BSX_DCC_02
            } else if ("3" == name) {
                bussiness = IpfsApi.BSX_DCC_03
            } else if ("4" == name) {
                bussiness = IpfsApi.BSX_DCC_04
            } else if ("5" == name) {
                bussiness = IpfsApi.BSX_DCC_05
            } else if ("6" == name) {
                bussiness = IpfsApi.BSX_DCC_06
            } else if ("7" == name) {
                bussiness = IpfsApi.BSX_DCC_07
            } else if ("8" == name) {
                bussiness = IpfsApi.BSX_DCC_08
            } else if ("9" == name) {
                bussiness = IpfsApi.BSX_DCC_09
            } else if ("10" == name) {
                bussiness = IpfsApi.BSX_DCC_10
            }
        } /*else if ("ETH" == assetCode) {
            bussiness = ""
        }*/

        binding.btBuy.setOnClickListener {
            if (canBuy) {

                if ("DCC" == assetCode) {
                    startActivity(Intent(this, BsxDccBuyActivity::class.java)
                            .putExtra("contractAddress", contractAddress)
                            .putExtra("name", name))
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
            ss = agent.getBsxSaleInfo(contractAddress)
        }

        ss.subscribeOnIo().doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }.subscribe({
                    var ss = BytesUtils.encodeString(it.result)//.replace(" ","")
                    saleInfo = ss.toBean(SaleInfo::class.java)
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
            ss = agent.getBsxMinAmountPerHand(contractAddress)
        }

        ss.subscribeOnIo().doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }.subscribe({

                    if ("DCC" == assetCode) {
                        minAmountPerHand = BytesUtils.encodeStringsimple(it.result).toString()
                    } else {
                        minAmountPerHand = BytesUtils.encodeStringsimple2(it.result)
                    }

                    MINBUYAMOUNT = minAmountPerHand

                    binding.minAmountPerHandDCC = minAmountPerHand + assetCode

                    getBsxInvestCeilAmount()
                }, {
                    LogUtils.i(it.message)
                })
    }

    private fun getBsxInvestCeilAmount() {

        var ss: Single<EthJsonRpcResponse<String>>

        if ("DCC" == assetCode) {
            ss = BsxOperations.getBsxInvestCeilAmount(bussiness)
        } else {
            ss = agent.getBsxInvestCeilAmount(contractAddress)
        }

        ss.subscribeOnIo().doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }.subscribe({

                    if ("DCC" == assetCode) {
                        totalAmount = encodeStringsimple(it.result).toString()
                    } else {
                        totalAmount = encodeStringsimple2(it.result).toString()
                    }

                    binding.tvProductlimit.text = ("" + totalAmount + assetCode)
                    getbiInvestedTotalAmount()
                }, {
                    LogUtils.i(it.message)
                })
    }

    private fun getbiInvestedTotalAmount() {

        var ss: Single<EthJsonRpcResponse<String>>

        if ("DCC" == assetCode) {
            ss = BsxOperations.investedBsxTotalAmount(bussiness)
        } else {
            ss = agent.investedBsxTotalAmount(contractAddress)
        }

        ss.subscribeOnIo().doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }.subscribe({
                    if ("DCC" == assetCode) {
                        lastAmount = encodeStringsimple(it.result).toString()
                    } else {
                        lastAmount = encodeStringsimple2(it.result)
                    }

                    val per = BigDecimal(totalAmount).subtract(BigDecimal(lastAmount)).multiply(BigDecimal("100")).divide(BigDecimal(totalAmount), 1, BigDecimal.ROUND_HALF_UP)

                    var res = ""

                    if ("DCC" == assetCode) {
                        res = BigDecimal(totalAmount).subtract(BigDecimal(lastAmount)).setScale(0).toPlainString()
                    } else {
                        res = BigDecimal(totalAmount).subtract(BigDecimal(lastAmount)).setScale(4).toPlainString()
                    }
                    binding.tvLostlimit.text = ("$res$assetCode ($per%）")
                    getBsxStatus()
                }, {
                    LogUtils.i(it.message)
                })
    }

    private fun getBsxStatus() {

        var ss: Single<EthJsonRpcResponse<String>>

        if ("DCC" == assetCode) {
            ss = BsxOperations.getBsxStatus(bussiness)
        } else {
            ss = agent.getBsxStatus(contractAddress)
        }

        ss.subscribeOnIo().doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }.subscribe({
                    var mystatu = BytesUtils.encodeStringstatu(it.result)

                    if (mystatu != 1) {
                        statu = "已结束"
                        canBuy = false
                    } else if (BigDecimal(minAmountPerHand).compareTo(BigDecimal(totalAmount).subtract(BigDecimal(lastAmount))) == 1) {
                        statu = "已售罄"
                        canBuy = false
                    } else {
                        statu = "认购"
                        canBuy = true
                    }
                    LASTAM = BigDecimal(totalAmount).subtract(BigDecimal(lastAmount)).toPlainString()
                    ONAME = saleInfo.name
                    runOnMainThread {
                        setButton()
                    }
                }, {
                    LogUtils.i(it.message)
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
