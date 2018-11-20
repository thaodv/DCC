package io.wexchain.android.dcc.modules.home

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.LoanProductDetailActivity
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
        adapter.getItemOnPos(i ).let {
            navigateTo(LoanProductDetailActivity::class.java) {
                putExtra(Extras.EXTRA_LOAN_PRODUCT_ID, it.id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initView()
        loadData()
    }

    private fun initView() {
        binding.header = getViewModel()
        adapter.lifecycleOwner = this
        binding.rvList.adapter = this@LoanActivity.adapter
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
                    adapter.setList(it.second)
                    if (item.first != null && item.second != null) {
                        showHeader(item.first!!, item.second!!)
                    }
                }
    }

    private fun showHeader(data: LoanRecordSummary, second: LoanRecord) {
        binding.header!!.run {
            isShow.set(true)
            loanTime.set(loanPeriodText(second).toString())
            loanNum.set(data.amount.toEngineeringString())
            orderNum.set(data.orderId.toString())
            status.set(loanStatusText(data.status).toString())
            currency.set(data.currency?.symbol)
            repay.set(if (second.expectLoanInterest == null || second.amount == null) {
                "---"
            } else {
                second.amount.add(second.expectLoanInterest).toString()
            })

            moreOrder.observe(this@LoanActivity, Observer {
                navigateTo(LoanRecordsActivity::class.java)
            })

            orderClick.observe(this@LoanActivity, Observer {
                navigateTo(LoanRecordDetailActivity::class.java) {
                    putExtra(Extras.EXTRA_LOAN_RECORD_ID, data.orderId)
                }
            })
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