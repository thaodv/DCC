package io.wexchain.android.dcc

import android.content.ClipData
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanRepayBinding
import io.wexchain.dccchainservice.domain.LoanRepaymentBill

class LoanRepayActivity : BindActivity<ActivityLoanRepayBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_loan_repay

    private val repayBill
        get() = intent.getSerializableExtra(Extras.EXTRA_LOAN_REPAY_BILL) as? LoanRepaymentBill

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        binding.btnConfirmRepay.setOnClickListener {
            binding.bill?.let {
                doConfirmRepay(it)
            }
        }
        binding.btnCopyAddress.setOnClickListener {
            val addr = binding.etRepayAddress.text?.toString()
            addr?.let {
                getClipboardManager().primaryClip = ClipData.newPlainText("还币地址", addr)
                toast("已复制到剪贴板")
            }
        }
    }

    private fun doConfirmRepay(bill: LoanRepaymentBill) {
        ScfOperations.withScfTokenInCurrentPassport(""){
            App.get().scfApi.confirmRepayment(it,bill.chainOrderId)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _->
                finish()
                navigateTo(LoanRepayResultActivity::class.java)
            }
    }

    private fun initData() {
        val bill = repayBill
        if (bill == null) {
            runOnUiThread { finish() }
        } else {
            binding.bill = bill
        }
    }
}
