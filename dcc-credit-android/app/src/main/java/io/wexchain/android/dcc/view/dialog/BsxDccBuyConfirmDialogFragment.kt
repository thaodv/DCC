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
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.BsxOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.bsx.BsxDccBuyActivity
import io.wexchain.android.dcc.modules.bsx.BsxDetailActivity
import io.wexchain.android.dcc.modules.bsx.BsxHoldingActivity
import io.wexchain.android.dcc.modules.repay.LoanRepayActivity
import io.wexchain.android.dcc.modules.repay.RePaymentErrorActivity
import io.wexchain.android.dcc.modules.repay.RepayingActivity
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.dcc.vm.TransactionConfirmVm
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.DialogConfirmBuyinvestmentBinding
import io.wexchain.dccchainservice.DccChainServiceException
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal

/**
 * Created by sisel on 2018/1/22.
 */
class BsxDccBuyConfirmDialogFragment : DialogFragment() {
    var isEdit = false
    var isRepayment = 0

    private lateinit var binding: DialogConfirmBuyinvestmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_confirm_buyinvestment, container, false)
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
        binding.btnConfirm.setOnClickListener {
            if (BigDecimal(vm.currentBanance) < binding.vm!!.tx.amount) {
                toast("持有量不足")
            } else {
                invest()
            }
        }
        return binding.root
    }

    val dccJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }
    val p = App.get().passportRepository.getCurrentPassport()!!
    var agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccJuzix)

    private fun invest() {

        BsxOperations
                .investBsx(io.wexchain.android.dcc.network.IpfsApi.BSX_DCC_02, binding.vm!!.tx.amount.scaleByPowerOfTen(18).toBigInteger(), Chain.JUZIX_PRIVATE)
                .doMain()
                .flatMap { txHash ->
                    App.get().chainGateway.getReceiptResult(txHash)
                            .compose(Result.checked())
                            .map {
                                if (!it.hasReceipt) {
                                    throw DccChainServiceException("no receipt yet")
                                }
                                it
                            }
                            .retryWhen(RetryWithDelay.createSimple(10, 5000L))
                            .map {
                                if (!it.approximatelySuccess) {
                                    throw DccChainServiceException()
                                }
                                txHash
                            }
                }
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                    dismiss()
                }
                .subscribe({
                    toast("交易成功")
                    hideLoadingDialog()
                    dismiss()
                    navigateTo(BsxHoldingActivity::class.java)
                    ActivityCollector.finishActivity(BsxDccBuyActivity::class.java)
                    ActivityCollector.finishActivity(BsxDetailActivity::class.java)
                    activity!!.finish()
                }, {
                    toast("交易失败")
                    hideLoadingDialog()
                    dismiss()
                })
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

        fun create(ethsTransactionScratch: EthsTransactionScratch, isEdit: Boolean = false, isRepayment: Int = 0): BsxDccBuyConfirmDialogFragment {
            return BsxDccBuyConfirmDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SCRATCH, ethsTransactionScratch)
                }
                this.isEdit = isEdit
                this.isRepayment = isRepayment
            }
        }
    }
}
