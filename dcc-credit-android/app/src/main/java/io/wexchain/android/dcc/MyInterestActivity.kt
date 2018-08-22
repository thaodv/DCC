package io.wexchain.android.dcc

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.alibaba.fastjson.JSON
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.domain.SaleInfo
import io.wexchain.android.dcc.tools.BintApi
import io.wexchain.android.dcc.tools.BytesUtils
import io.wexchain.android.dcc.tools.BytesUtils.encodeStringsimple
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityMyInterestBinding
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.api.domain.EthJsonRpcRequestBody
import java.math.BigDecimal


class MyInterestActivity : BindActivity<ActivityMyInterestBinding>() {

    companion object {
          var MINBUYAMOUNT=0
        var LASTAM=0
        var ONAME="私链DCC币生息1期"


    }
    var canBuy:Boolean=false

    override val contentLayoutId: Int = R.layout.activity_my_interest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setWindowExtended()
        initdatas()
        initclick()

    }

    private fun initdatas() {
        getbiSaleInfo()
         getbiminAmountPerHand()
        getbiInvestCeilAmount()
        //binding.saleInfo=saleInfo

    }

    override fun onResume() {
        super.onResume()
        initdatas()
    }

    private fun initclick() {
         binding.tvback.setOnClickListener {
             finish()
         }
        binding.tvHave.setOnClickListener {
            navigateTo(MyInterestDetailActivity::class.java)
        }
        setButton()
        binding.tvBuyit.setOnClickListener {
            if(canBuy){
                navigateTo(BuyInterestActivity::class.java)
            }else{

            }
        }
    }

    var totalAmount:Int=0
    var lastAmount:Int=0
    var gapAmount:Int=totalAmount-lastAmount
    fun  getbiInvestedTotalAmount() {
        val getAllowance = Erc20Helper.investedTotalAmount(BintApi.contract,"","")
        App.get(). bintApi.postStatus(
            EthJsonRpcRequestBody(
                method = "eth_call",
                params = listOf(getAllowance, "latest"),
                id = 1L
            )
        ). subscribeOn(AndroidSchedulers.mainThread()).subscribe(
            {
                lastAmount=encodeStringsimple(it.result)
                var ssss=BigDecimal((totalAmount-lastAmount)*100).divide(BigDecimal(totalAmount)).setScale(1)
                binding.lastamountDCC=(""+(totalAmount-lastAmount)+" DCC ("+ssss +"%）")
                Log.e("lastAmount",""+ encodeStringsimple(it.result))
                Log.e("lastamountDCC",""+ ssss)
                checkStatu()
            },{
                it.printStackTrace()
            }
        )
    }
    fun  getbiInvestCeilAmount() {
        val getAllowance = Erc20Helper.getInvestCeilAmount(BintApi.contract,"","")
        App.get(). bintApi.postStatus(
            EthJsonRpcRequestBody(
                method = "eth_call",
                params = listOf(getAllowance, "latest"),
                id = 1L
            )
        ). subscribeOn(AndroidSchedulers.mainThread()).subscribe(
            {
                totalAmount=encodeStringsimple(it.result)
                binding.totalamountDCC=(""+totalAmount+" DCC")
                Log.e("totalamountDCC",""+ encodeStringsimple(it.result))
                getbiInvestedTotalAmount()

            },{
                it.printStackTrace()
            }
        )
    }
    var minAmountPerHand:Int=0
    fun  getbiminAmountPerHand() {
        val getAllowance = Erc20Helper.getMinAmountPerHando(BintApi.contract,"","")
        App.get().bintApi.postStatus(
            EthJsonRpcRequestBody(
                method = "eth_call",
                params = listOf(getAllowance, "latest"),
                id = 1L
            )
        ). subscribeOn(AndroidSchedulers.mainThread()).subscribe(
            {
                 minAmountPerHand= encodeStringsimple(it.result)
                MINBUYAMOUNT=minAmountPerHand
              //  saleInfo.minAmountPerHand=minAmountPerHand
                 binding.minAmountPerHandDCC=(""+minAmountPerHand+" DCC")
                Log.e("encodeStringsimple",""+ encodeStringsimple(it.result))
                checkStatu()
            },{
                it.printStackTrace()
            }
        )
    }
    lateinit var saleInfo:SaleInfo
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
                 checkStatu()
             },{
                 it.printStackTrace()
             }
         )
        Log.e("getbiSaleInfo", "getbiSaleInfo")

    }

    var statu="已结束"
    fun checkStatu(){
        require(null!=saleInfo)
        if( System.currentTimeMillis() >saleInfo.closeTime){
            statu="已结束"

        }else if(minAmountPerHand > (totalAmount-lastAmount)){
            statu="已售罄"
        }else{
            statu="认购"
            canBuy=true
        }
        LASTAM= totalAmount-lastAmount
        ONAME=saleInfo.name
        setButton()
    }
    fun setButton(){
        binding.tvBuyit.text=statu
        if(canBuy){
            binding.tvBuyit.setBackgroundColor(Color.parseColor("#FF6766CC"))
        }else{
            binding.tvBuyit.setBackgroundColor(Color.parseColor("#B2484848"))
        }
    }
}
