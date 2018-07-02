package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.*
import io.wexchain.android.dcc.constant.RequestCodes
import com.wexmarket.android.passport.ResultCodes
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.view.adapter.BottomMoreItemsAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.TransactionRecordAdapter
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.vm.BottomMoreVm
import io.wexchain.android.dcc.vm.DigitalCurrencyVm
import io.wexchain.android.dcc.vm.TransactionListVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDigitalCurrencyBinding
import io.wexchain.dcc.databinding.ItemBottomMoreTextBinding
import io.wexchain.digitalwallet.*
import io.wexchain.digitalwallet.util.gweiTowei
import java.math.BigInteger

/**
 * Digital currency detail
 * holding / quote / transactions
 */
class DigitalCurrencyActivity : BindActivity<ActivityDigitalCurrencyBinding>(), ItemViewClickListener<EthsTransaction> {

    override val contentLayoutId: Int = R.layout.activity_digital_currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        initVm()
        initBtn()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        super.onCreateOptionsMenu(menu)
//        menuInflater.inflate(R.menu.menu_share_send, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        return when (item?.itemId) {
//            R.id.action_share_send -> {
//                showShare()
//                return true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

//    private fun showShare() {
//        ShareHelper.showShareWithCapture(this, window.decorView)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.CREATE_TRANSACTION -> {
                val txId = data?.getStringExtra(Extras.EXTRA_DIGITAL_TRANSACTION_ID)
                val scratch = data?.getSerializableExtra(Extras.EXTRA_DIGITAL_TRANSACTION_SCRATCH) as? EthsTransactionScratch
                if (resultCode == ResultCodes.RESULT_OK && txId != null && scratch != null) {
                    App.get().assetsRepository.pushPendingTx(EthsTransaction(
                            digitalCurrency = scratch.currency,
                            txId = txId,
                            from = scratch.from,
                            to = scratch.to,
                            nonce=scratch.nonce,
                            amount = scratch.currency.toIntExact(scratch.amount),
                            transactionType = EthsTransaction.TYPE_TRANSFER,
                            remarks = scratch.remarks,
                            time = -1L,
                            blockNumber = -1L,
                            gas = scratch.gasLimit,
                            gasUsed = BigInteger.ZERO,
                            gasPrice = gweiTowei(scratch.gasPrice),
                            status = EthsTransaction.Status.PENDING
                    ))
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onItemClick(item: EthsTransaction?, position: Int, viewId: Int) {
        item ?: return
        startActivity(Intent(this, DigitalTransactionDetailActivity::class.java).apply {
            putExtra(Extras.EXTRA_DIGITAL_TRANSACTION, item)
        })
    }

    private fun initVm() {
        val dc = getDc()
        val address = App.get().passportRepository.currPassport.value!!.address
        val vm = ViewModelProviders.of(this).get(address, DigitalCurrencyVm::class.java).apply {
            ensure(address, dc, this@DigitalCurrencyActivity)
        }
        vm.fetchDataFailEvent.observe(this, Observer {
            toast("网络异常,请稍后重试")
        })
        vm.confirmEvent.observe(this, Observer {
            it ?: return@Observer
            CustomDialog(this)
                    .apply {
                        textContent = it.first
                        withPositiveButton {
                            it.second(true)
                            true
                        }
                        withNegativeButton()
                    }
                    .assembleAndShow()
        })
        vm.selectedChangedEvent.observe(this, Observer {
            it?.let {
                if (it) {
                    toast("添加成功")
                } else {
                    toast("已删除")
                }
            }
        })
        binding.vm = vm
        val txListVm = getViewModel<TransactionListVm>("${dc.contractAddress}:$address").apply {
            ensure(dc, address, false)
        }
        txListVm.viewMoreEvent.observe(this, Observer {
            toTransactionListActivity()
        })
        txListVm.pendingTxDoneEvent.observe(this, Observer {
            vm.load()
        })
        binding.txList = txListVm
        val adapter = TransactionRecordAdapter(address, this)
        val bm = BottomMoreVm().apply {
            noMoreHint.set(getText(R.string.no_more))
            loadMoreHint.set(getText(R.string.load_more))
        }
        val bottomMoreItemsAdapter = BottomMoreItemsAdapter(adapter, object : BottomMoreItemsAdapter.BottomViewProvider {

            override fun inflateBottomView(parent: ViewGroup): View {
                parent ?: throw IllegalArgumentException()
                val b = DataBindingUtil.inflate<ItemBottomMoreTextBinding>(LayoutInflater.from(parent.context), R.layout.item_bottom_more_text, parent, false)
                b.bm = bm
                b.tvLoadMore.setOnClickListener {
                    toTransactionListActivity()
                }
                return b.root
            }

            override fun onBind(bottomView: View?, position: Int) {
            }
        })
        binding.sectionTransactions!!.rvTransactions.adapter = bottomMoreItemsAdapter
        txListVm.list.observe(this, Observer {
            val list3 = it?.subList(0, minOf(it.size, 3)) ?: emptyList()
            adapter.setList(list3)
            txListVm.empty.set(list3.isEmpty())
            bm.hasMore.set(it != null && it.size > 3)
        })
    }

    private fun toTransactionListActivity() {
        startActivity(Intent(this, DigitalTransactionsActivity::class.java).apply {
            putExtra(Extras.EXTRA_DIGITAL_CURRENCY, getDc())
        })
    }

    private fun getDc() =
            intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY)!! as DigitalCurrency

    private fun initBtn() {
        binding.btnToTransfer.setOnClickListener {
            toCreateTransaction()
        }
        binding.btnToCollect.setOnClickListener {
            startActivity(Intent(this, PassportAddressActivity::class.java))
        }
    }

    private fun toCreateTransaction() {
        val dc = getDc()
        if(dc.chain == Chain.JUZIX_PRIVATE && dc.symbol == Currencies.FTC.symbol){
//            App.get().ftcApi.getFeeRate()
//                    .compose(Result.checked())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnSubscribe {
//                        showLoadingDialog()
//                    }
//                    .doFinally {
//                        hideLoadingDialog()
//                    }
//                    .subscribe({
//                        startActivityForResult(Intent(this, CreateTransactionActivity::class.java).apply {
//                            putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc)
//                            putExtra(Extras.EXTRA_FTC_TRANSFER_FEE_RATE, it)
//                        }, RequestCodes.CREATE_TRANSACTION)
//                    },{
//                        //todo
//                        stackTrace(it)
//                    })
        }else {
            startActivityForResult(Intent(this, CreateTransactionActivity::class.java).apply {
                putExtra(Extras.EXTRA_DIGITAL_CURRENCY, dc)
            }, RequestCodes.CREATE_TRANSACTION)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.vm!!.load()
        binding.txList?.let {
            it.showAll.set(false)
        }
    }
}
