package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import com.wexmarket.android.passport.ResultCodes
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.trans.vm.Private2PublicConfirmVm
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.DialogConfirmPrivate2publicBinding
import io.wexchain.digitalwallet.EthsTransactionScratch

/**
 * Created by sisel on 2018/1/22.
 */
class Private2PublicConfirmDialogFragment : DialogFragment() {
    var isEdit = false

    private lateinit var binding: DialogConfirmPrivate2publicBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_confirm_private2public, container, false)
        val scratch = getScratch()
        val poundge = getPoundge()

        val app = App.get()
        val passport = app.passportRepository.getCurrentPassport()!!
        val vm = Private2PublicConfirmVm(scratch, passport, app.assetsRepository, poundge!!, isEdit)
        vm.syncProtect(this)
        vm.txSentEvent.observe(this, Observer {
            it?.let {
                toast("转账申请提交成功")
                activity?.setResult(ResultCodes.RESULT_OK, Intent().apply {
                    putExtra(Extras.EXTRA_DIGITAL_TRANSACTION_SCRATCH, it.first)
                    putExtra(Extras.EXTRA_DIGITAL_TRANSACTION_ID, it.second)
                })
                activity?.finish()
            }
        })
        vm.txSendFailEvent.observe(this, Observer {
            it?.let {
                toast("转账提交失败")
            }
            if (isEdit) activity?.finish()

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
        binding.ibtClose.setOnClickListener {
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

    private fun getPoundge() = arguments?.getString("poundge")

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

        fun create(ethsTransactionScratch: EthsTransactionScratch, poundge: String, isEdit: Boolean = false): Private2PublicConfirmDialogFragment {
            return Private2PublicConfirmDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SCRATCH, ethsTransactionScratch)
                    putString("poundge", poundge)
                }
                this.isEdit = isEdit
            }
        }
    }
}
