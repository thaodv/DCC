package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.vm.CertFeeConfirmVm
import io.wexchain.android.dcc.vm.currencyToDisplayStr
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.DialogCertFeeBinding
import io.wexchain.digitalwallet.Currencies
import java.math.BigInteger

class CertFeeConfirmDialog : DialogFragment() {

    private var onConfirm: (() -> Unit)? = null

    private var fee: Long = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<DialogCertFeeBinding>(inflater, R.layout.dialog_cert_fee, container, false)
        val vm = getViewModel<CertFeeConfirmVm>().apply {
            confirmEvent.observe(this@CertFeeConfirmDialog, Observer{
                onConfirm?.invoke()
                dismiss()
            })
            val feeStr = "${Currencies.DCC.toDecimalAmount(BigInteger.valueOf(this@CertFeeConfirmDialog.fee)).currencyToDisplayStr()} DCC"
            fee.set(feeStr)
            loadHolding()
        }
        binding.vm = vm
        binding.ibClose.setOnClickListener {
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
        fun create(fee:Long,onConfirm: () -> Unit): CertFeeConfirmDialog {
            val certFeeConfirmDialog = CertFeeConfirmDialog()
            certFeeConfirmDialog.onConfirm = onConfirm
            certFeeConfirmDialog.fee = fee
            return certFeeConfirmDialog
        }
    }
}