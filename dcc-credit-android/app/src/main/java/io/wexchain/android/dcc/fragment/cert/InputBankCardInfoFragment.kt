package io.wexchain.android.dcc.fragment.cert

import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.NoDoubleClickListener
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.InputBankCardInfoVm
import io.wexchain.android.dcc.vm.currencyToDisplayStr
import io.wexchain.android.dcc.vm.domain.BankCardInfo
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentInputBankCardBinding
import io.wexchain.dcc.databinding.ItemBankInfoBinding
import io.wexchain.dccchainservice.ChainGateway
import io.wexchain.dccchainservice.domain.Bank
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.digitalwallet.Currencies
import java.math.BigInteger

class InputBankCardInfoFragment : BindFragment<FragmentInputBankCardBinding>(), ItemViewClickListener<Bank> {
    override val contentLayoutId: Int = R.layout.fragment_input_bank_card

    //    private lateinit var rxLocation: RxLocationManager
    private lateinit var rxPermissions: RxPermissions

    private val adapter = SimpleDataBindAdapter<ItemBankInfoBinding, Bank>(
            layoutId = R.layout.item_bank_info,
            variableId = BR.bank,
            itemViewClickListener = this@InputBankCardInfoFragment
    )

    private var listener: Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = activity!!
//        rxLocation = RxLocationManager(ctx)
        rxPermissions = RxPermissions(ctx)
        App.get().marketingApi.getBankList()
                .checkonMain()
                .subscribeBy {
                    adapter.setList(it)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = getViewModel<InputBankCardInfoVm>()/*.apply {
            submitEvent.observe(this@InputBankCardInfoFragment, Observer {
                it?.let { listener?.onProceed(it) }
            })
            informationIncompleteEvent.observe(this@InputBankCardInfoFragment, Observer {
                it?.let { toast("请输入完整信息") }
            })
        }*/

        binding.btNext.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                val info = viewModel.checkAndBuildBankCardInfo()
                if (info == null) {
                    toast("请输入完整信息")
                } else {
                    listener?.onProceed(info)
                }
            }
        })


        binding.vm = viewModel
        binding.inputBank.setOnClickListener {
            showOrHideBanksSheet()
        }
        App.get().chainGateway.getExpectedFee(ChainGateway.BUSINESS_BANK_CARD)
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    val fee = it.toLong()
                    viewModel.certFee.set("${getString(R.string.cost_of_verification)}${Currencies.DCC.toDecimalAmount(BigInteger.valueOf(fee)).currencyToDisplayStr()} DCC")
                }
    }

    override fun onItemClick(item: Bank?, position: Int, viewId: Int) {
        binding.vm?.bank?.set(item)
        bankDialog?.dismiss()
    }

    private var bankDialog: Dialog? = null

    private fun showOrHideBanksSheet() {
        var dialog = bankDialog
        if (dialog == null) {
            dialog = Dialog(context!!, R.style.FullWidthWhiteDialog).apply {
                setContentView(R.layout.dialog_banks)
                val dialogWindow = window
                val lp = dialogWindow.attributes
                val d = context.resources.displayMetrics
                lp.width = d.widthPixels
                lp.height = (d.heightPixels * 0.6).toInt()
                lp.gravity = Gravity.BOTTOM
                dialogWindow.attributes = lp
                this.findViewById<View>(R.id.btn_cancel)!!.setOnClickListener { dismiss() }
                this.findViewById<RecyclerView>(R.id.rv_list)!!.adapter = adapter
            }
            bankDialog = dialog
        }
        if (dialog.isShowing) {
            dialog.dismiss()
        } else {
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.bank_account_verification)

//        checkPreconditions()
    }

    interface Listener {
        fun onProceed(bankCardInfo: BankCardInfo)
    }

    var certType: String
        get() = arguments?.getString(Extras.EXTRA_CERT_TYPE) ?: ""
        set(value) {
            if (arguments == null) {
                arguments = Bundle()
            }
            arguments!!.putString(Extras.EXTRA_CERT_TYPE, value)
        }

    companion object {
        fun create(listener: Listener): InputBankCardInfoFragment {
            val fragment = InputBankCardInfoFragment()
            fragment.listener = listener
            return fragment
        }
    }
}
