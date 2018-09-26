package io.wexchain.android.dcc

import android.os.Bundle
import android.util.Log
import com.alibaba.fastjson.JSON
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.domain.SaleInfo
import io.wexchain.android.dcc.tools.BintApi
import io.wexchain.android.dcc.tools.BytesUtils
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyInterestdetailBinding
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.api.domain.EthJsonRpcRequestBody
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal
import java.text.DecimalFormat

class MyInterestDetailActivity : BindActivity<ActivityMyInterestdetailBinding>() {

    var totalAmount: Int = 0
    var lastAmount: Int = 0
    var pRate = BigDecimal(0.007671)
    var exPro = BigDecimal(0)
    var mystatu: Int = 4

    lateinit var saleInfo: SaleInfo

    override val contentLayoutId: Int = R.layout.activity_my_interestdetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        getbiInvestedTotalAmount()
        getbiStatues()
        getbiSaleInfo()
    }

    private fun getbiInvestedTotalAmount() {
        val getAllowance = Erc20Helper.investedBsxAmountMapping(BintApi.contract, App.get().passportRepository.getCurrentPassport()!!.address)
        App.get().bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).doMain().subscribe(
                {
                    lastAmount = BytesUtils.encodeStringsimple(it.result)
                    if (lastAmount == 0) {
                        binding.isEmpty = true
                    }

                    val df = DecimalFormat("###,###")
                    //开始格式化
                    Log.e("lastAmount", "" + BytesUtils.encodeStringsimple(it.result))

                    binding.totalamountDCC = df.format(lastAmount)
                    exPro = (pRate * BigDecimal(lastAmount)).setScale(2, BigDecimal.ROUND_DOWN)//*BigDecimal(saleInfo.period)
                    binding.expDCC = "+ " + exPro//df2.format( exPro  )
                    var to = exPro + BigDecimal(lastAmount)
                    binding.expallDCC = "" + to
                },
                {
                    it.printStackTrace()
                }
        )
    }

    private fun getbiStatues() {
        val getAllowance = Erc20Helper.getBsxStatus(BintApi.contract)
        App.get().bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).doMain().subscribe(
                {
                    mystatu = BytesUtils.encodeStringstatu(it.result)

                    Log.e("getbiStatues", "" + mystatu)
                    setUI()
                },
                {
                    it.printStackTrace()
                }
        )
    }

    private fun getbiSaleInfo() {
        val getAllowance = Erc20Helper.getBsxSaleInfo(BintApi.contract)
        App.get().bintApi.postStatus(
                EthJsonRpcRequestBody(
                        method = "eth_call",
                        params = listOf(getAllowance, "latest"),
                        id = 1L
                )
        ).doMain().subscribe(
                {
                    var ss = BytesUtils.encodeString(it.result)//.replace(" ","")
                    saleInfo = JSON.parseObject(ss, SaleInfo::class.java)

                    binding.saleInfo = saleInfo
                },
                {
                    it.printStackTrace()
                }
        )
    }

    private fun setUI() {
        binding.totalamountDCClable = "在投本金（DCC）"
        if (mystatu == 4) {//已关闭
            binding.totalamountstatu = "已结束"
            binding.isClose = true
        } else {

            binding.isClose = false
            if (mystatu == 0 || mystatu == 1) {
                binding.totalamountstatu = "募集中"
            }
            if (mystatu == 2 || mystatu == 3) {
                binding.totalamountstatu = "收益中"
            }
        }
    }

}
