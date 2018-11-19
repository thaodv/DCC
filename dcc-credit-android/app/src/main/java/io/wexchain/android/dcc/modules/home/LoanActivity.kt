package io.wexchain.android.dcc.modules.home

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.LoanProductDetailActivity
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.repay.LoanRecordDetailActivity
import io.wexchain.android.dcc.modules.repay.LoanRecordsActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.BindingViewHolder
import io.wexchain.android.dcc.view.adapter.ClickAwareHolder
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.android.dcc.vm.ViewModelHelper.loanPeriodText
import io.wexchain.android.dcc.vm.ViewModelHelper.loanStatusText
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityLoanBinding
import io.wexchain.dcc.databinding.ItemLoanProductBinding
import io.wexchain.dccchainservice.domain.LoanProduct
import io.wexchain.dccchainservice.domain.LoanRecord
import io.wexchain.dccchainservice.domain.LoanRecordSummary
import io.wexchain.ipfs.utils.io_main
import kotlinx.android.synthetic.main.activity_loan.*

/**
 *Created by liuyang on 2018/9/21.
 */
class LoanActivity : BindActivity<ActivityLoanBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_loan

    private val adapter = Adapter(this::onItemClick)

    private fun onItemClick(i: Int, j: Int) {
        adapter.getItemOnPos(i - 2).let {
            navigateTo(LoanProductDetailActivity::class.java) {
                putExtra(Extras.EXTRA_LOAN_PRODUCT_ID, it.id)
            }
        }
    }

    private val header by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        LayoutInflater.from(this).inflate(R.layout.loan_header, service_toot, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initView()
        loadData()
    }

    private fun initView() {
        adapter.lifecycleOwner = this
        binding.rvList.run {
            adapter = this@LoanActivity.adapter
            layoutManager = LinearLayoutManager(this@LoanActivity)
            setPullRefreshEnabled(false)
            addHeaderView(header)
        }
    }


    private fun loadData() {
        Singles.zip(
                ScfOperations.withScfTokenInCurrentPassport {
                    App.get().scfApi.queryOrderPage(it, 0L, 10L)
                }.flatMap {
                    val item = it.items
                    if (item.isNotEmpty()) {
                        ScfOperations
                                .withScfTokenInCurrentPassport {
                                    App.get().scfApi.getLoanRecordById(it, item[0].orderId)
                                }.map {
                                    item[0] to it
                                }
                    } else {
                        Single.just(null to null)
                    }
                },
                App.get().scfApi.queryLoanProductsByLenderCode().check())
                .io_main()
                .subscribeBy {
                    val item = it.first
                    if (item.first != null && item.second != null) {
                        showHeader(item.first!!, item.second!!)
                    }
                    adapter.setList(it.second)
                }
    }

    private fun showHeader(data: LoanRecordSummary, second: LoanRecord) {
        header.findViewById<View>(R.id.cv_header).visibility = View.VISIBLE
        header.findViewById<TextView>(R.id.tv_time).text = loanPeriodText(second)

        header.findViewById<TextView>(R.id.tv_order).text = data.orderId.toString()
        header.findViewById<TextView>(R.id.tv_money).text = data.amount.toEngineeringString()
        header.findViewById<TextView>(R.id.tv_currency).text = data.currency?.symbol
        header.findViewById<TextView>(R.id.tv_repay).text =
                if (second.expectLoanInterest == null || second.amount == null) {
                    "---"
                } else {
                    second.amount.add(second.expectLoanInterest).toString()
                }
        header.findViewById<TextView>(R.id.tv_status).text = loanStatusText(data.status)
        header.findViewById<Button>(R.id.btn_more).onClick {
            navigateTo(LoanRecordsActivity::class.java)
        }
        header.findViewById<View>(R.id.cv_header).onClick {
            navigateTo(LoanRecordDetailActivity::class.java) {
                putExtra(Extras.EXTRA_LOAN_RECORD_ID, data.orderId)
            }
        }
    }


    private class Adapter(val onPosClick: (Int, Int) -> Unit) :
            DataBindAdapter<ItemLoanProductBinding, LoanProduct>(
                    R.layout.item_loan_product,
                    itemDiffCallback = defaultItemDiffCallback()
            ) {
        override fun bindData(binding: ItemLoanProductBinding, item: LoanProduct?) {
            binding.product = item
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ItemLoanProductBinding> {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemLoanProductBinding = DataBindingUtil.inflate(layoutInflater, layout, parent, false)
            return ClickAwareHolder(binding, onPosClick)
        }
    }

}