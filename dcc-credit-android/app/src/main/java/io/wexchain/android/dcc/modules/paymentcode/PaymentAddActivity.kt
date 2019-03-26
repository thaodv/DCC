package io.wexchain.android.dcc.modules.paymentcode

import android.content.Intent
import android.os.Bundle
import android.view.View
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.RequestCodes
import io.wexchain.android.common.constant.ResultCodes
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.common.view.datepicker.DateFormatUtils
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.trustpocket.TrustChooseCoinActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.dialog.CustomDatePicker
import io.wexchain.android.dcc.view.payment.PaymentAddAmountStyle
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentAddBinding
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal

class PaymentAddActivity : BindActivity<ActivityPaymentAddBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_payment_add

    private lateinit var mTimerPicker: CustomDatePicker

    private var mAmount: String = ""

    private var mExpiredTimeValue = ""

    private var mPayStyle: String = "0"  // 0-固定金额 1-用户自定义金额

    private lateinit var mCode: String
    private lateinit var mUrl: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initTimerPicker()

        binding.name = "请选择"
        binding.tvDeadtimeValue.text = "长期有效"

        binding.paystyle.setFixChecked()
        binding.paystyle.setSelfUnChecked()
        binding.paystyle.etAmount = "0.00000001"

        binding.paystyle.setOnPaymentStyleClickLinester(object : PaymentAddAmountStyle.OnPaymentStyleClickLinester {
            override fun fix() {
                mPayStyle = "0"
            }

            override fun self() {
                mPayStyle = "1"
            }

            override fun question() {

            }
        })

        binding.tvDeadtimeValue.onClick {
            mTimerPicker.show(DateFormatUtils.long2Str(System.currentTimeMillis(), true))
        }

        binding.rlChoose.onClick {
            startActivityForResult(
                    Intent(this, TrustChooseCoinActivity::class.java).putExtra("use", "paymentChoose"),
                    RequestCodes.CHOOSE_PAYMENT_CODE
            )
        }

        binding.btCreate.onClick {

            if ("" == binding.etTitle.text.toString()) {
                toast("请填写标题")
            } else if ("" == binding.etDescription.text.toString()) {
                toast("请填写说明")
            } else if ("请选择" == binding.tvName.text.toString()) {
                toast("请选择币种")
            } else {
                if ("0" == mPayStyle) {
                    if ("" == binding.paystyle.etAmount) {
                        toast("请输入金额")
                    } else {
                        mAmount = binding.paystyle.etAmount
                        createGoods(binding.tvName.text.toString(), mAmount.toBigDecimal(), binding.etTitle.text.toString(), binding.etDescription.text.toString())
                    }
                } else {
                    mAmount = ""
                    createGoods(binding.tvName.text.toString(), mAmount.toBigDecimal(), binding.etTitle.text.toString(), binding.etDescription.text.toString())
                }
            }
        }
    }

    private fun createGoods(assetCode: String, amount: BigDecimal, title: String, description: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.createGoods(it, assetCode, amount, title, description, mExpiredTimeValue).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    navigateTo(PaymentShareActivity::class.java) {
                        putExtra("id", it.id)
                        putExtra("code", it.assetCode)
                    }
                }, {
                    toast(it.message.toString())
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimerPicker.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CHOOSE_PAYMENT_CODE -> {
                if (resultCode == ResultCodes.RESULT_OK) {

                    mCode = data!!.getStringExtra("code")
                    mUrl = data!!.getStringExtra("url")

                    binding.ivUrl.visibility = View.VISIBLE
                    binding.url = mUrl
                    binding.name = mCode


                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initTimerPicker() {
        val beginTime = "2018/01/01 00:00"
        //String endTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        val currentTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true)
        val endTime = "2049/12/31 00:00"


        // 通过日期字符串初始化日期，格式请用：yyyy/MM/dd HH:mm
        mTimerPicker = CustomDatePicker(this, object : CustomDatePicker.Callback {
            override fun cancel() {
                binding.tvDeadtimeValue.text = "长期有效"
                mExpiredTimeValue = ""
            }

            override fun onTimeSelected(timestamp: Long) {
                binding.tvDeadtimeValue.text = DateFormatUtils.long2Str(timestamp, true)
                mExpiredTimeValue = DateFormatUtils.long2Str(timestamp, true)
            }
        }, beginTime, endTime)
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true)
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime(true)
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true)
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true)
    }
}
