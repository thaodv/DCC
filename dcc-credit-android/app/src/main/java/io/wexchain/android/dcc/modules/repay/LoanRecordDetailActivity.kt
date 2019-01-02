package io.wexchain.android.dcc.modules.repay

import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.Pop
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.LoanAgreementActivity
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.home.LoanActivity
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanRecordDetailBinding
import io.wexchain.dccchainservice.domain.LoanRecord
import io.wexchain.dccchainservice.domain.LoanStatus

class LoanRecordDetailActivity : BindActivity<ActivityLoanRecordDetailBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_loan_record_detail

    private val loanRecordId
        get() = intent.getLongExtra(Extras.EXTRA_LOAN_RECORD_ID, -1L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initData()
        binding.btnAction.setOnClickListener {
            performAction(binding.order)
        }
        binding.flLoanContract.setOnClickListener {
            binding.order?.orderId?.let {
                navigateTo(LoanAgreementActivity::class.java) {
                    putExtra(Extras.EXTRA_LOAN_CHAIN_ORDER_ID, it)
                }
            }
        }

    }

    private fun performAction(order: LoanRecord?) {
        when (order?.status) {
            LoanStatus.INVALID -> {
            }//todo
            LoanStatus.CREATED -> {
            }//todo
            LoanStatus.CANCELLED -> {
            }//todo
            LoanStatus.AUDITING -> {
            }
            LoanStatus.APPROVED -> {
            }
            LoanStatus.REJECTED -> applyAgain()
            LoanStatus.FAILURE -> applyAgain()
            LoanStatus.REPAID -> applyAgain()
            LoanStatus.DELIVERED -> toRepay()
            LoanStatus.RECEIVIED -> toRepay()
            null -> {
            }
        }
    }

    private fun toRepay() {
        binding.order?.let {
            if (it.earlyRepayAvailable && !it.allowRepayPermit) {
                //early repay not allowed
            } else {
                navigateTo(ReviewRepayActivity::class.java) {
                    putExtra(Extras.EXTRA_LOAN_CHAIN_ORDER_ID, it.orderId)
                }
            }
        }
    }

    private fun applyAgain() {
        navigateTo(LoanActivity::class.java)
    }

    private fun initData() {
        val recordId = loanRecordId
        if (recordId == -1L) {
            postOnMainThread {
                finish()
            }
        } else {
            loadLoanOrderDetail(recordId)
        }
    }

    private fun loadLoanOrderDetail(recordId: Long) {
        ScfOperations
                .withScfTokenInCurrentPassport {
                    App.get().scfApi.getLoanRecordById(it, recordId)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe({
                    val tmp = it
                    binding.order = tmp
                    /*if ((tmp.status == LoanStatus.RECEIVIED || tmp.status == LoanStatus.DELIVERED) && tmp.earlyRepayAvailable && tmp.allowRepayPermit) {
                        binding.tvRepaymentProcess.visibility = View.VISIBLE
                    } else {
                        binding.tvRepaymentProcess.visibility = View.INVISIBLE
                    }*/
                }, {
                    Pop.toast(it.message ?: "系统错误", this)
                })
    }
}
