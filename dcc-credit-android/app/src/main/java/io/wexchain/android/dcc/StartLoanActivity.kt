package io.wexchain.android.dcc

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.warkiz.widget.IndicatorSeekBar
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.view.EATextView
import io.wexchain.android.dcc.vm.StartLoanVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityStartLoanBinding
import io.wexchain.dcc.databinding.EiCustomIndicatorBinding
import io.wexchain.dccchainservice.domain.LoanProduct

class StartLoanActivity : BindActivity<ActivityStartLoanBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_start_loan

    private val product
        get() = intent.getSerializableExtra(Extras.EXTRA_LOAN_PRODUCT) as? LoanProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        val p = product
        if (p == null) {
            postOnMainThread {
                //todo
                finish()
            }
        } else {
            val vm = getViewModel<StartLoanVm>().apply {
                product.set(p)
            }
            binding.vm = vm

            binding.isbFee.apply {
                min = p.dccFeeScope.first().toFloat()
                max = p.dccFeeScope.last().toFloat()
                setOnSeekChangeListener(object : IndicatorSeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: IndicatorSeekBar?,
                        progress: Int,
                        progressFloat: Float,
                        fromUserTouch: Boolean
                    ) {
                        val e = progress > 160
                        if (isExceeded != e) {

                        }
                        indicator.getmContentView().apply {
                            findViewById<EATextView>(R.id.tv_dcc_insufficient_notice).isExceeded = e
                            findViewById<EATextView>(R.id.isb_progress).isExceeded = e
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?, thumbPosOnTick: Int) {
                    }

                    override fun onSectionChanged(
                        seekBar: IndicatorSeekBar?,
                        thumbPosOnTick: Int,
                        textBelowTick: String?,
                        fromUserTouch: Boolean
                    ) {
                    }

                    override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    }

                })
            }
        }
    }
}
