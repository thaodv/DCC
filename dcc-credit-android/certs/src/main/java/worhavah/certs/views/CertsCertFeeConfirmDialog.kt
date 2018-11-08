package worhavah.certs.views

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import io.wexchain.android.common.getViewModel
import io.wexchain.digitalwallet.Currencies
import worhavah.certs.R
import worhavah.certs.databinding.CertsDialogCertFeeBinding
import worhavah.certs.vm.CertFeeConfirmVm
import java.math.BigInteger
import java.math.RoundingMode

class CertsCertFeeConfirmDialog : DialogFragment() {

    private var onConfirm: (() -> Unit)? = null

    private var fee: BigInteger = BigInteger("0")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<CertsDialogCertFeeBinding>(inflater, R.layout.certs_dialog_cert_fee, container, false)
        val vm = getViewModel<CertFeeConfirmVm>().apply {
            val feeStr = "${Currencies.DCC.toDecimalAmount(this@CertsCertFeeConfirmDialog.fee).setScale(4, RoundingMode.DOWN).toPlainString()} DCC"
            fee.set(feeStr)
            feec= Currencies.DCC.toDecimalAmount(this@CertsCertFeeConfirmDialog.fee).setScale(4, RoundingMode.DOWN)
            confirmEvent.observe(this@CertsCertFeeConfirmDialog, Observer{

                onConfirm?.invoke()
                dismiss()
            })

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
        fun create(fee:BigInteger,onConfirm: () -> Unit): CertsCertFeeConfirmDialog {
            val certFeeConfirmDialog = CertsCertFeeConfirmDialog()
            certFeeConfirmDialog.onConfirm = onConfirm
            certFeeConfirmDialog.fee = fee
            return certFeeConfirmDialog
        }
    }
}
