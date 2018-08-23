package io.wexchain.android.dcc

import android.os.Bundle
import android.util.Log
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.domain.SaleInfo
import io.wexchain.android.dcc.tools.BintApi
import io.wexchain.android.dcc.tools.BytesUtils
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyInterestdetailBinding
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.api.domain.EthJsonRpcRequestBody
import java.math.BigDecimal
import java.text.DecimalFormat

class MyInterestDetailActivity : BindActivity<ActivityMyInterestdetailBinding>() {
    lateinit var saleInfo:SaleInfo

    override val contentLayoutId: Int = R.layout.activity_my_interestdetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setWindowExtended()
        initToolbar()
        initdatas()
        initclick()

        // setUI()
    }
    var pRate=BigDecimal(0.007671)

    private fun setUI() {
       if(mystatu==0){//已关闭
            binding.totalamountDCClable="投资本金（DCC）"
           binding.totalamountstatu="已结束"
           binding.isClose=true
       }else{
           binding.totalamountDCClable="在投本金（DCC）"
           binding.totalamountstatu="募集中"
           binding.isClose=false
       }
    }

    private fun initclick() {

    }
    private fun initdatas() {
        getbiInvestedTotalAmount()
        getbiStatues()
        getbiSaleInfo()



        //binding.saleInfo=saleInfo

    }
    var mystatu:Int=0
    fun  getbiStatues() {
        val getAllowance = Erc20Helper.getStatus(BintApi.contract,"","")
         App.get(). bintApi.postStatus(
            EthJsonRpcRequestBody(
                method = "eth_call",
                params = listOf(getAllowance, "latest"),
                id = 1L
            )
        ).subscribeOn(AndroidSchedulers.mainThread()).subscribe(
             {
                 mystatu= BytesUtils.encodeStringstatu(it.result)

                 Log.e("getbiStatues",""+ mystatu)
                 setUI()

             },{
                 it.printStackTrace()
             }
         )

    }

    var totalAmount:Int=0
    var lastAmount:Int=0


var exPro=BigDecimal(0)
    fun  getbiInvestedTotalAmount() {
        val getAllowance = Erc20Helper.investedAmountMapping(BintApi.contract,App.get().passportRepository.getCurrentPassport()!!.address )
        App.get(). bintApi.postStatus(
            EthJsonRpcRequestBody(
                method = "eth_call",
                params = listOf(getAllowance, "latest"),
                id = 1L
            )
        ). subscribeOn(AndroidSchedulers.mainThread()).subscribe(
            {
                lastAmount= BytesUtils.encodeStringsimple(it.result)
                if(lastAmount==0){
                    binding.isEmpty=true
                }

                val df = DecimalFormat("###,###")
                val df2 = DecimalFormat("###,###.00")
                //开始格式化
             var sss=df.format(lastAmount)  //"1,234,567"
                Log.e("lastAmount",""+ BytesUtils.encodeStringsimple(it.result))

                binding.totalamountDCC=sss
                Log.e("sss",""+ sss)
                exPro=(pRate*BigDecimal(lastAmount)).setScale(2,BigDecimal.ROUND_DOWN)//*BigDecimal(saleInfo.period)
                Log.e("exPro",""+ exPro)
                binding.expDCC="+ "+exPro//df2.format( exPro  )
                var to=exPro+BigDecimal(lastAmount)
                binding.expallDCC=""+to//df2.format(exPro+BigDecimal(lastAmount.toString()))
            },{
                it.printStackTrace()
            }
        )
    }

    fun  getBiInvestedTotalAmount() {
        val getAllowance = Erc20Helper.investedTotalAmount(BintApi.contract,"","")
        App.get(). bintApi.postStatus(
            EthJsonRpcRequestBody(
                method = "eth_call",
                params = listOf(getAllowance, "latest"),
                id = 1L
            )
        ).blockingGet()

    }



    var minAmountPerHand:Int=0

    fun  getbiSaleInfo() {
        val getAllowance = Erc20Helper.getSaleInfo(BintApi.contract,"","")
        /*     val response =App.get(). bintApi.postStatus(
            EthJsonRpcRequestBody(
                method = "eth_call",
                params = listOf(getAllowance, "latest"),
                id = 1L
            )
        ).blockingGet()
        var ss=response.result
         Log.e("getbiSaleInfo", BytesUtils.encodeString(ss ))*/
        App.get(). bintApi.postStatus(
            EthJsonRpcRequestBody(
                method = "eth_call",
                params = listOf(getAllowance, "latest"),
                id = 1L
            )
        ). subscribeOn(AndroidSchedulers.mainThread()).subscribe(
            {
                var ss=BytesUtils.encodeString(it.result )//.replace(" ","")
                saleInfo  = JSON.parseObject(ss, SaleInfo::class.java)

                binding.saleInfo=saleInfo
                //binding.tvProfit.setText(saleInfo.annualRateP())
                Log.e("getbiSaleInfo", saleInfo.name)

            },{
                it.printStackTrace()
            }
        )
        Log.e("getbiSaleInfo", "getbiSaleInfo")

    }


}
