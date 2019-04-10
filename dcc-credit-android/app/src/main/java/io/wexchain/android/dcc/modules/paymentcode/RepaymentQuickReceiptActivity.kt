package io.wexchain.android.dcc.modules.paymentcode

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import io.reactivex.Single
import io.wexchain.android.common.BR
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityRepaymentQuickReceiptBinding
import io.wexchain.dcc.databinding.ItemPaymentReceiptBinding
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.domain.payment.QueryGoodsViewPageBean

class RepaymentQuickReceiptActivity : BindActivity<ActivityRepaymentQuickReceiptBinding>(), ItemViewClickListener<QueryGoodsViewPageBean> {
    override fun onItemClick(item: QueryGoodsViewPageBean?, position: Int, viewId: Int) {
        if (QueryGoodsViewPageBean.GoodsBean.Status.ACTIVE == item!!.goods.status) {
            navigateTo(PaymentReceiptDetailActivity::class.java) {
                putExtra("id", item.goods.id)
            }
        } else {
            navigateTo(PaymentReceiptDetailTimeoutActivity::class.java) {
                putExtra("id", item.goods.id)
            }
        }
    }

    private val adapter = SimpleDataBindAdapter<ItemPaymentReceiptBinding, QueryGoodsViewPageBean>(
            layoutId = R.layout.item_payment_receipt,
            variableId = BR.bean,
            itemViewClickListener = this@RepaymentQuickReceiptActivity
    )

    override val contentLayoutId: Int get() = R.layout.activity_repayment_quick_receipt

    var vm: QueryGoodsVm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        findViewById<TextView>(R.id.tv_add).onClick {
            navigateTo(PaymentAddActivity::class.java)
        }

        vm = getViewModel()
        val srl = binding.srlList

        srl.setOnRefreshListener { sr ->
            vm!!.refresh { sr.finishRefresh() }
        }
        srl.setOnLoadMoreListener { sr ->
            vm!!.loadNext { sr.finishLoadMore() }
        }

        vm!!.checkData.observe(this, Observer {
            val status = binding.llEmpty.visibility
            if (status != it!!) {
                binding.llEmpty.visibility = it
            }
        })

        binding.rvList.adapter = adapter
        binding.vm = vm


        binding.tvMyReceipt.onClick {
            binding.tvMyReceipt.setTextColor(resources.getColor(R.color.FF6144CC))
            binding.vMyReceipt.visibility = View.VISIBLE
            binding.tvHistoryReceipt.setTextColor(resources.getColor(R.color.common_7F6144CC))
            binding.vHistoryReceipt.visibility = View.INVISIBLE

            vm!!.status = "ACTIVE"
            binding.srlList.autoRefresh()
        }

        binding.tvHistoryReceipt.onClick {
            binding.tvMyReceipt.setTextColor(resources.getColor(R.color.common_7F6144CC))
            binding.vMyReceipt.visibility = View.INVISIBLE
            binding.tvHistoryReceipt.setTextColor(resources.getColor(R.color.FF6144CC))
            binding.vHistoryReceipt.visibility = View.VISIBLE
            vm!!.status = "CLOSED"
            binding.srlList.autoRefresh()
        }


    }

    override fun onResume() {
        super.onResume()
        binding.srlList.autoRefresh()
    }

    class QueryGoodsVm : PagedVm<QueryGoodsViewPageBean>() {

        var status: String = "ACTIVE" // CLOSED

        override fun loadPage(page: Int): Single<PagedList<QueryGoodsViewPageBean>> {

            return GardenOperations.refreshToken {
                App.get().marketingApi.queryGoodsViewPage(it, status, page, 20).check()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_payment_trans_records, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.trade_records -> {
                navigateTo(PaymentTransRecordsActivity::class.java) {
                    putExtra("goodsId", "")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
