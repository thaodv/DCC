package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.vm.ConfirmLoanVm
import io.wexchain.android.dcc.vm.domain.LoanScratch
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.DialogConfirmLoanSubmitBinding
import io.wexchain.dccchainservice.domain.LoanProduct
import java.math.BigDecimal

class ConfirmLoanSubmitDialog : DialogFragment() {

    private lateinit var scratch: LoanScratch
    private var onConfirm: ((LoanScratch) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<DialogConfirmLoanSubmitBinding>(
            inflater,
            R.layout.dialog_confirm_loan_submit,
            container,
            false
        )
        binding.setLifecycleOwner(this)
        val vm = getViewModel<ConfirmLoanVm>().apply {
            setScratch(this@ConfirmLoanSubmitDialog.scratch)
            loadHolding()
        }
        binding.vm = vm

        //events
        binding.ibClose.setOnClickListener { dismiss() }
        binding.btnConfirm.setOnClickListener {
            onConfirm?.invoke(scratch)
            dismiss()
        }
        return binding.root
    }

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
        fun create(
            scratch: LoanScratch,
            onConfirm: ((LoanScratch) -> Unit)? = null
        ): ConfirmLoanSubmitDialog {
            return ConfirmLoanSubmitDialog().apply {
                this.scratch = scratch
                this.onConfirm = onConfirm
            }
        }
    }
}