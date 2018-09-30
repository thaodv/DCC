package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import com.wexmarket.android.passport.ResultCodes
import io.wexchain.android.common.base.ActivityCollector
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.repay.LoanRepayActivity
import io.wexchain.android.dcc.modules.repay.RePaymentErrorActivity
import io.wexchain.android.dcc.modules.repay.RepayingActivity
import io.wexchain.android.dcc.vm.TransactionConfirmVm
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.DialogConfirmTransactionBinding
import io.wexchain.digitalwallet.EthsTransactionScratch

/**
 * Created by sisel on 2018/1/22.
 */
class TransactionConfirmDialogFragment : DialogFragment() {
    var isEdit = false
    var isRepayment = 0

    private lateinit var binding: DialogConfirmTransactionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_confirm_transaction, container, false)
        val scratch = getScratch()
        val app = App.get()
        val passport = app.passportRepository.getCurrentPassport()!!
        val vm = TransactionConfirmVm(scratch, passport, app.assetsRepository, isEdit)
        vm.syncProtect(this)
        vm.txSentEvent.observe(this, Observer {
            it?.let {
                if (isRepayment == 0) {
                    toast("转账申请提交成功")
                    activity?.setResult(ResultCodes.RESULT_OK, Intent().apply {
                        putExtra(Extras.EXTRA_DIGITAL_TRANSACTION_SCRATCH, it.first)
                        putExtra(Extras.EXTRA_DIGITAL_TRANSACTION_ID, it.second)
                    })
                    activity?.finish()
                } else {
                    startActivity(Intent(App.get(), RepayingActivity::class.java))
                    ActivityCollector.finishActivity(LoanRepayActivity::class.java)
                    activity?.finish()
                }

            }
        })
        vm.txSendFailEvent.observe(this, Observer {
            it?.let {
                if (isRepayment == 0) {

                    toast("转账提交失败")
                    if (isEdit) activity?.finish()
                } else {
                    startActivity(Intent(App.get(), RePaymentErrorActivity::class.java))
                    ActivityCollector.finishActivity(LoanRepayActivity::class.java)
                }
            }


        })
        vm.busySendingEvent.observe(this, Observer {
            it?.let {
                if (it) {
                    showLoadingDialog()
                } else {
                    hideLoadingDialog()
                }
            }
        })
        vm.notEnoughFundsEvent.observe(this, Observer {
            CustomDialog(context!!).apply {
                textContent = getString(R.string.insufficient_balance_holding)
            }.assembleAndShow()
        })
        VerifyProtectFragment.serve(vm, this) { childFragmentManager }
        binding.vm = vm
        binding.ibClose.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.vm!!.loadHoldings()
    }


    private var loadingDialog: FullScreenDialog? = null

    fun showLoadingDialog() {
        var d = loadingDialog
        if (d == null) {
            d = FullScreenDialog.createLoading(context!!)
            loadingDialog = d
        }
        d.show()
    }

    fun hideLoadingDialog() {
        loadingDialog?.hide()
    }

    private fun getScratch() =
            arguments?.getSerializable(ARG_SCRATCH)!! as EthsTransactionScratch

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert_Dcc)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window.apply {
            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        return dialog
    }

    companion object {
        const val ARG_SCRATCH = "argument_transaction_scratch"

        fun create(ethsTransactionScratch: EthsTransactionScratch, isEdit: Boolean = false, isRepayment: Int = 0): TransactionConfirmDialogFragment {
            return TransactionConfirmDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SCRATCH, ethsTransactionScratch)
                }
                this.isEdit = isEdit
                this.isRepayment = isRepayment
            }
        }
    }
}
