package io.wexchain.android.dcc.modules.trans.activity

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.domain.AccrossTransDetail
import io.wexchain.dccchainservice.util.DateUtil

class TransPrivate2PublicDetailActivity : BaseCompatActivity() {

    private val id get() = intent.getLongExtra("id", 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans_private2public_detail)
        initToolbar()

        var tv_originAmount = findViewById<TextView>(R.id.tv_originAmount)
        var tv_createdTime = findViewById<TextView>(R.id.tv_createdTime)
        var tv_status = findViewById<TextView>(R.id.tv_status)
        var tv_feeAmount = findViewById<TextView>(R.id.tv_feeAmount)
        var tv_originAmount2 = findViewById<TextView>(R.id.tv_originAmount2)
        var tv_originReceiverAddress = findViewById<TextView>(R.id.tv_originReceiverAddress)
        var tv_beneficiaryAddress = findViewById<TextView>(R.id.tv_beneficiaryAddress)
        var tv_originTxHash = findViewById<TextView>(R.id.tv_originTxHash)
        var tv_originBlockNumber = findViewById<TextView>(R.id.tv_originBlockNumber)
        var tv_originTradeTime = findViewById<TextView>(R.id.tv_originTradeTime)
        var tv_destAmount = findViewById<TextView>(R.id.tv_destAmount)
        var tv_beneficiaryAddress2 = findViewById<TextView>(R.id.tv_beneficiaryAddress2)
        var tv_destPayerAddress = findViewById<TextView>(R.id.tv_destPayerAddress)
        var tv_destTxHash = findViewById<TextView>(R.id.tv_destTxHash)
        var tv_destBlockNumber = findViewById<TextView>(R.id.tv_destBlockNumber)
        var tv_destTradeTime = findViewById<TextView>(R.id.tv_destTradeTime)
        var ll_in = findViewById<LinearLayout>(R.id.ll_in)

        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getChainExchangeDetail(it, id.toString()).observeOn(AndroidSchedulers.mainThread())

                }.subscribe({
                    tv_originAmount.text = ViewModelHelper.getTransCount(it.originAmount)

                    tv_status.text = ViewModelHelper.accrossStatus(it.status)
                    tv_feeAmount.text = "手续费:" + ViewModelHelper.getTransCount(it.feeAmount) + " DCC"
                    tv_originAmount2.text = "-" + ViewModelHelper.getTransCount(it.originAmount) + " DCC"
                    tv_originReceiverAddress.text = it.originReceiverAddress
                    tv_beneficiaryAddress.text = it.beneficiaryAddress

                    val ssb = SpannableStringBuilder(it.originTxHash)
                    ssb.setSpan(URLSpan(BuildConfig.ACCROSS_DETAIL + it.originTxHash), 0, ssb.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    tv_originTxHash.text = ssb
                    tv_originTxHash.movementMethod = LinkMovementMethod.getInstance()

                    tv_originBlockNumber.text = it.originBlockNumber.toString()
                    tv_originTradeTime.text = DateUtil.getStringTime(it.originTradeTime, "yyyy-MM-dd HH:mm:ss")

                    if (it.status.equals(AccrossTransDetail.Status.DELIVERED)) {
                        ll_in.visibility = View.VISIBLE
                        tv_destAmount.text = "+" + ViewModelHelper.getTransCount(it.destAmount) + " DCC"
                        tv_beneficiaryAddress2.text = it.beneficiaryAddress
                        tv_destPayerAddress.text = it.destPayerAddress

                        val ssb1 = SpannableStringBuilder(it.destTxHash)
                        ssb1.setSpan(URLSpan(BuildConfig.ACCROSS_DETAIL + it.destTxHash), 0, ssb1.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                        tv_destTxHash.text = ssb1
                        tv_destTxHash.movementMethod = LinkMovementMethod.getInstance()

                        tv_destBlockNumber.text = it.destBlockNumber.toString()
                        tv_destTradeTime.text = DateUtil.getStringTime(it.destTradeTime, "yyyy-MM-dd HH:mm:ss")

                        tv_createdTime.text = DateUtil.getStringTime(it.createdTime, "yyyy-MM-dd HH:mm:ss")

                    } else {
                        ll_in.visibility = View.GONE
                    }

                }, {})

    }
}
