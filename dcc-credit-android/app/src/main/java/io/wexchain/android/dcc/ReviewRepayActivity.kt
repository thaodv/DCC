package io.wexchain.android.dcc

import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityReviewRepayBinding

class ReviewRepayActivity : BindActivity<ActivityReviewRepayBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_review_repay

    private val loanOrderId
        get() = intent.getLongExtra(Extras.EXTRA_LOAN_CHAIN_ORDER_ID, -1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        binding.btnConfirm.setOnClickListener {
            binding.bill?.let {
                navigateTo(LoanRepayActivity::class.java) {
                    putExtra(Extras.EXTRA_LOAN_REPAY_BILL, it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        val id = loanOrderId
        if (id == -1L) {
            postOnMainThread {
                finish()
            }
        } else {
            getRepaymentBill(id)
        }
    }

    private fun getRepaymentBill(id: Long) {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getRepaymentBill(it, id)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe({
                    binding.bill = it
                }, {
                    Pop.toast(it.message ?: "系统错误", this)
                })
    }
}
