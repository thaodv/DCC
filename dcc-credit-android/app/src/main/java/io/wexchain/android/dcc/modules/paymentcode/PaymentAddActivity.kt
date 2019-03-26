package io.wexchain.android.dcc.modules.paymentcode

import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.onClick
import io.wexchain.android.common.view.datepicker.DateFormatUtils
import io.wexchain.android.dcc.view.dialog.CustomDatePicker
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentAddBinding

class PaymentAddActivity : BindActivity<ActivityPaymentAddBinding>() {

    override val contentLayoutId: Int get() = R.layout.activity_payment_add

    private lateinit var mTimerPicker: CustomDatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initTimerPicker()

        binding.rlChooseDeadtime.onClick {
            mTimerPicker.show(binding.tvDeadtimeValue.text.toString())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mTimerPicker.onDestroy()
    }

    private fun initTimerPicker() {
        val beginTime = "2018-01-01 00:00"
        //String endTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        val currentTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true)
        val endTime = "2049-12-31 00:00"

        binding.tvDeadtimeValue.text = currentTime

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = CustomDatePicker(this, object : CustomDatePicker.Callback {
            override fun onTimeSelected(timestamp: Long) {
                binding.tvDeadtimeValue.text = DateFormatUtils.long2Str(timestamp, true)
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
