package io.wexchain.android.dcc.modules.bsx

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.BsxOperations
import io.wexchain.android.dcc.domain.SaleInfo
import io.wexchain.android.dcc.network.IpfsApi
import io.wexchain.android.dcc.tools.BytesUtils
import io.wexchain.android.dcc.tools.BytesUtils.encodeStringsimple
import io.wexchain.android.dcc.tools.BytesUtils.encodeStringsimple2
import io.wexchain.android.dcc.tools.toBean
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityBsxDetailBinding
import io.wexchain.digitalwallet.Currencies
import io.wexchain.ipfs.utils.doBack
import io.wexchain.ipfs.utils.doMain
import io.wexchain.ipfs.utils.io_main
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
            }
        }

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
        Single.just(assetCode)
                .flatMap {
                    if ("DCC" == assetCode) {
                        BsxOperations.getBsxSaleInfo(bussiness)
                    } else {
                        agent.getBsxSaleInfo(contractAddress)
                    }
                }
                .io_main()
                .withLoading()
                .map {
                    val ss = BytesUtils.encodeString(it.result)
                    saleInfo = ss.toBean(SaleInfo::class.java)
                    binding.saleInfo = saleInfo
                    assetCode
                }
                .doBack()
                .flatMap {
                    if ("DCC" == it) {
                        BsxOperations.getBsxMinAmountPerHand(bussiness)
                    } else {
                        agent.getBsxMinAmountPerHand(contractAddress)
                    }
                }
                .doMain()
                .map {
                    if ("DCC" == assetCode) {
                        minAmountPerHand = BytesUtils.encodeStringsimple(it.result).toString()
                    } else {
                        minAmountPerHand = BytesUtils.encodeStringsimple2(it.result)
                    }

                    MINBUYAMOUNT = minAmountPerHand

                    binding.minAmountPerHandDCC = minAmountPerHand + assetCode
                    assetCode
                }
                .doBack()
                .flatMap {
                    if ("DCC" == it) {
                        BsxOperations.getBsxInvestCeilAmount(bussiness)
                    } else {
                        agent.getBsxInvestCeilAmount(contractAddress)
                    }
                }
                .doMain()
                .map {
                    totalAmount = if ("DCC" == assetCode) {
                        encodeStringsimple(it.result).toString()
                    } else {
                        encodeStringsimple2(it.result).toString()
                    }
                    binding.tvProductlimit.text = ("" + totalAmount + assetCode)
                }
                .doBack()
                .flatMap {
                    if ("DCC" == assetCode) {
                        BsxOperations.investedBsxTotalAmount(bussiness)
                    } else {
                        agent.investedBsxTotalAmount(contractAddress)
                    }

                }
                .doMain()
                .map {
                    lastAmount = if ("DCC" == assetCode) {
                        encodeStringsimple(it.result).toString()
                    } else {
                        encodeStringsimple2(it.result)
                    }

                    val per = BigDecimal(totalAmount).subtract(BigDecimal(lastAmount)).multiply(BigDecimal("100")).divide(BigDecimal(totalAmount), 1, BigDecimal.ROUND_HALF_UP)

                    val res = if ("DCC" == assetCode) {
                        BigDecimal(totalAmount).subtract(BigDecimal(lastAmount)).setScale(0).toPlainString()
                    } else {
                        BigDecimal(totalAmount).subtract(BigDecimal(lastAmount)).setScale(4).toPlainString()
                    }

                    binding.tvLostlimit.text = ("$res$assetCode ($per%）")
                    assetCode
                }
                .doBack()
                .flatMap {
                    if ("DCC" == it) {
                        BsxOperations.getBsxStatus(bussiness)
                    } else {
                        agent.getBsxStatus(contractAddress)
                    }
                }
                .doMain()
                .subscribeBy {
                    val mystatu = BytesUtils.encodeStringstatu(it.result)
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

                    setButton()
                }
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
