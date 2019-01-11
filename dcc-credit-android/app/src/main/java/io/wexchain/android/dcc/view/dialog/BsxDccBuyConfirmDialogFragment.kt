package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import io.wexchain.android.common.base.ActivityCollector
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.BsxOperations
import io.wexchain.android.dcc.modules.bsx.BsxDccBuyActivity
import io.wexchain.android.dcc.modules.bsx.BsxDetailActivity
import io.wexchain.android.dcc.modules.bsx.BsxHoldingActivity
import io.wexchain.android.dcc.tools.MultiChainHelper
import io.wexchain.android.dcc.tools.RetryWithDelay
import io.wexchain.android.dcc.tools.StringUtils
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
    lateinit var name: String

    private lateinit var binding: DialogConfirmBuyinvestmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_confirm_buyinvestment, container, false)
        val scratch = getScratch()
        val app = App.get()
        val passport = app.passportRepository.getCurrentPassport()!!
        val vm = TransactionConfirmVm(scratch, passport, app.assetsRepository, isEdit)
        vm.syncProtect(this)
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
                invest(name)
            }
        }
        return binding.root
    }

    val dccJuzix = MultiChainHelper.dispatch(Currencies.DCC).first { it.chain == Chain.JUZIX_PRIVATE }
    val p = App.get().passportRepository.getCurrentPassport()!!
    var agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccJuzix)

    private fun invest(name: String) {

        var bussiness = ""

        bussiness = StringUtils.getBsxName(name)

        /*if ("1" == name) {
            bussiness = IpfsApi.BSX_DCC_01
        } else if ("2" == name) {
            bussiness = IpfsApi.BSX_DCC_02
        } else if ("3" == name) {
            bussiness = IpfsApi.BSX_DCC_03
        } else if ("4" == name) {
            bussiness = IpfsApi.BSX_DCC_04
        } else if ("5" == name) {
            bussiness = IpfsApi.BSX_DCC_05
        } else if ("6" == name) {
            bussiness = IpfsApi.BSX_DCC_06
        } else if ("7" == name) {
            bussiness = IpfsApi.BSX_DCC_07
        } else if ("8" == name) {
            bussiness = IpfsApi.BSX_DCC_08
        } else if ("9" == name) {
            bussiness = IpfsApi.BSX_DCC_09
        } else if ("10" == name) {
            bussiness = IpfsApi.BSX_DCC_10
        } else if ("11" == name) {
            bussiness = IpfsApi.BSX_DCC_11
        } else if ("12" == name) {
            bussiness = IpfsApi.BSX_DCC_12
        } else if ("13" == name) {
            bussiness = IpfsApi.BSX_DCC_13
        } else if ("14" == name) {
            bussiness = IpfsApi.BSX_DCC_14
        } else if ("15" == name) {
            bussiness = IpfsApi.BSX_DCC_15
        } else if ("16" == name) {
            bussiness = IpfsApi.BSX_DCC_16
        } else if ("17" == name) {
            bussiness = IpfsApi.BSX_DCC_17
        } else if ("18" == name) {
            bussiness = IpfsApi.BSX_DCC_18
        } else if ("19" == name) {
            bussiness = IpfsApi.BSX_DCC_19
        } else if ("20" == name) {
            bussiness = IpfsApi.BSX_DCC_20
        }*/

        BsxOperations
                .investBsx(bussiness, binding.vm!!.tx.amount.scaleByPowerOfTen(18).toBigInteger(), Chain.JUZIX_PRIVATE)
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
                            .retryWhen(RetryWithDelay.createSimple(10, 3000))
                            .map {
                                if (!it.approximatelySuccess) {
                                    throw DccChainServiceException()
                                }
                                txHash
                            }
                }
                .doMain()
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

        fun create(ethsTransactionScratch: EthsTransactionScratch, name: String, isEdit: Boolean = false, isRepayment: Int = 0): BsxDccBuyConfirmDialogFragment {
            return BsxDccBuyConfirmDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SCRATCH, ethsTransactionScratch)
                }
                this.name = name
                this.isEdit = isEdit
                this.isRepayment = isRepayment
            }
        }
    }
}
