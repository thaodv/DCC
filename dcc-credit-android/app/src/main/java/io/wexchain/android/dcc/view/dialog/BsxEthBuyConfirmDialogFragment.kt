package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.ActivityCollector
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.modules.bsx.BsxDccBuyActivity
import io.wexchain.android.dcc.modules.bsx.BsxDetailActivity
import io.wexchain.android.dcc.modules.bsx.BsxHoldingActivity
import io.wexchain.android.dcc.modules.repay.LoanRepayActivity
import io.wexchain.android.dcc.modules.repay.RePaymentErrorActivity
import io.wexchain.android.dcc.modules.repay.RepayingActivity
import io.wexchain.android.dcc.tools.LogUtils
import io.wexchain.android.dcc.tools.TransHelper
import io.wexchain.android.dcc.vm.TransactionConfirmVm
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.DialogConfirmBuyEthInvestmentBinding
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.Erc20Helper
import io.wexchain.digitalwallet.EthsTransactionScratch
import io.wexchain.digitalwallet.util.gweiTowei
import io.wexchain.ipfs.utils.io_main
import org.web3j.abi.FunctionEncoder
import java.math.BigDecimal

/**
 * Created by sisel on 2018/1/22.
 */
class BsxEthBuyConfirmDialogFragment : DialogFragment() {

    private lateinit var binding: DialogConfirmBuyEthInvestmentBinding

    lateinit var vm: TransactionConfirmVm

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_confirm_buy_eth_investment, container, false)
        val scratch = getScratch()
        val app = App.get()
        val passport = app.passportRepository.getCurrentPassport()!!
        vm = TransactionConfirmVm(scratch, passport, app.assetsRepository)
        vm.syncProtect(this)
        vm.txSentEvent.observe(this, Observer {
            it?.let {
                startActivity(Intent(App.get(), RepayingActivity::class.java))
                ActivityCollector.finishActivity(LoanRepayActivity::class.java)
                activity?.finish()

            }
        })
        vm.txSendFailEvent.observe(this, Observer {
            it?.let {
                startActivity(Intent(App.get(), RePaymentErrorActivity::class.java))
                ActivityCollector.finishActivity(LoanRepayActivity::class.java)
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

    val p = App.get().passportRepository.getCurrentPassport()!!

    val assetsRepository = App.get().assetsRepository

    val dccPublic = Currencies.Ethereum

    val agent = App.get().assetsRepository.getDigitalCurrencyAgent(dccPublic)

    private fun invest() {

        //val approve = Erc20Helper.investBsx(binding.vm!!.tx.amount.scaleByPowerOfTen(18).toBigInteger())
        val approve = Erc20Helper.investEthBsx()

        LogUtils.i("approve-1", FunctionEncoder.encode(approve))

        LogUtils.i("approve-2", binding.vm!!.tx.amount.scaleByPowerOfTen(18).toBigInteger().toString())

        agent.getNonce(p.address)
                .flatMap {
                    agent.sendTransferTransaction(
                            p.credential,
                            getContractAddress(),
                            binding.vm!!.tx.amount.scaleByPowerOfTen(18).toBigInteger(),
                            gweiTowei(binding.vm!!.tx.gasPrice),
                            binding.vm!!.tx.gasLimit,
                            FunctionEncoder.encode(approve))
                }
                .io_main()
                .subscribeBy({
                    toast("交易失败")
                    dismiss()
                }, {
                    getScratch().nonce = it.first

                    getScratch().to = getContractAddress()
                    getScratch().amount = binding.vm!!.tx.amount
                    getScratch().gasPrice = binding.vm!!.tx.gasPrice
                    getScratch().gasLimit = binding.vm!!.tx.gasLimit
                    getScratch().remarks = FunctionEncoder.encode(approve)

                    TransHelper.afterTransSuc(getScratch(), it.second)
                    toast("转账提交成功")
                    hideLoadingDialog()
                    dismiss()
                    navigateTo(BsxHoldingActivity::class.java)
                    ActivityCollector.finishActivity(BsxDccBuyActivity::class.java)
                    ActivityCollector.finishActivity(BsxDetailActivity::class.java)
                    activity!!.finish()
                })


        /*agent.getNonce(p.address)
                .io_main()
                .flatMap {
                    LogUtils.i("invest-nonce", it.toString())
                    val rawTransaction = RawTransaction.createTransaction(
                            it,
                            gweiTowei(binding.vm!!.tx.gasPrice),
                            binding.vm!!.tx.gasLimit,
                            getContractAddress(),
                            binding.vm!!.tx.amount.scaleByPowerOfTen(18).toBigInteger(),
                            FunctionEncoder.encode(approve)
                    )
                    val signed = Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, p.credential))
                    agent.sendRawTransaction(signed)
                            .doMain()
                }
                .flatMap { txHash ->
                    agent.transactionReceipt(txHash)
                            .retryWhen(
                                    RetryWithDelay.createSimple(
                                            10,
                                            3000L
                                    )
                            )
                }
                .doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }.doFinally {
                    hideLoadingDialog()
                }
                .subscribe({
                    if ("0x1" == it.status) {
                        toast("交易成功")
                        dismiss()
                        navigateTo(BsxHoldingActivity::class.java)
                        ActivityCollector.finishActivity(BsxEthBuyActivity::class.java)
                        ActivityCollector.finishActivity(BsxDetailActivity::class.java)
                        activity!!.finish()
                    } else {
                        toast("交易失败")
                        dismiss()
                    }
                }, {
                    stackTrace(it)
                    toast("交易失败")
                    dismiss()
                })*/


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
        dismiss()
    }

    private fun getScratch() =
            arguments?.getSerializable(ARG_SCRATCH)!! as EthsTransactionScratch

    private fun getContractAddress() =
            arguments?.getSerializable(CONTRACTADDRESS)!! as String

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
        const val CONTRACTADDRESS = "contractAddress"

        fun create(ethsTransactionScratch: EthsTransactionScratch, contractAddress: String): BsxEthBuyConfirmDialogFragment {
            return BsxEthBuyConfirmDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SCRATCH, ethsTransactionScratch)
                    putSerializable(CONTRACTADDRESS, contractAddress)
                }
            }
        }
    }
}
