package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.*
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.view.adapter.BottomMoreItemsAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.TransactionRecordAdapter
import io.wexchain.android.dcc.vm.BottomMoreVm
import io.wexchain.android.dcc.vm.TransactionListVm
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityDigitalTransactionsBinding
import io.wexchain.auth.databinding.ItemBottomMoreTextBinding
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.EthsTransaction

class DigitalTransactionsActivity : BindActivity<ActivityDigitalTransactionsBinding>(), ItemViewClickListener<EthsTransaction> {
    override val contentLayoutId: Int = R.layout.activity_digital_transactions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)

        val digitalCurrency = getDc()

        val address = App.get().passportRepository.currPassport.value!!.address
        val txListVm = getViewModel<TransactionListVm>("${digitalCurrency.contractAddress}:$address").apply {
            ensure(digitalCurrency, address, true)
        }
        binding.txList = txListVm
        initRvList(address, txListVm)
    }

    override fun onResume() {
        super.onResume()
        binding.txList?.let {
            it.showAll.set(true)
        }
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
//
//    private fun showShare() {
//        ShareHelper.showShareWithCapture(this, window.decorView)
//    }

    private fun initRvList(address: String, txListVm: TransactionListVm) {
        val adapter = TransactionRecordAdapter(address, this)
        val bm = BottomMoreVm().apply {
            noMoreHint.set(getText(R.string.no_more))
            hasMore.set(false)
        }
        val bottomMoreItemsAdapter = BottomMoreItemsAdapter(adapter, object : BottomMoreItemsAdapter.BottomViewProvider {

            override fun inflateBottomView(parent: ViewGroup): View {
                parent ?: throw IllegalArgumentException()
                val b = DataBindingUtil.inflate<ItemBottomMoreTextBinding>(LayoutInflater.from(parent.context), R.layout.item_bottom_more_text, parent, false)
                b.bm = bm
                return b.root
            }

            override fun onBind(bottomView: View?, position: Int) {
            }
        })
        binding.sectionTransactions!!.rvTransactions.adapter = bottomMoreItemsAdapter
        txListVm.list.observe(this, Observer {
            adapter.setList(it)
            txListVm.empty.set(it?.isEmpty() ?: true)
        })
    }

    override fun onItemClick(item: EthsTransaction?, position: Int, viewId: Int) {
        item ?: return
        startActivity(Intent(this, DigitalTransactionDetailActivity::class.java).apply {
            putExtra(Extras.EXTRA_DIGITAL_TRANSACTION, item)
        })
    }


    private fun getDc() =
            intent.getSerializableExtra(Extras.EXTRA_DIGITAL_CURRENCY)!! as DigitalCurrency
}
