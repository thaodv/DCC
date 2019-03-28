package io.wexchain.android.dcc.modules.paymentcode

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.reactivex.Single
import io.wexchain.android.common.BR
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.android.dcc.vm.PagedVm
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPaymentTransRecordsBinding
import io.wexchain.dcc.databinding.ItemPaymentTransDetailBinding
import io.wexchain.dccchainservice.domain.PagedList
import io.wexchain.dccchainservice.domain.payment.QueryGoodsOrderPageBean

class PaymentTransRecordsActivity : BindActivity<ActivityPaymentTransRecordsBinding>(), ItemViewClickListener<QueryGoodsOrderPageBean> {

    override fun onItemClick(item: QueryGoodsOrderPageBean?, position: Int, viewId: Int) {

    }

    override val contentLayoutId: Int get() = R.layout.activity_payment_trans_records


    private val adapter = SimpleDataBindAdapter<ItemPaymentTransDetailBinding, QueryGoodsOrderPageBean>(
            layoutId = R.layout.item_payment_trans_detail,
            variableId = BR.bean,
            itemViewClickListener = this@PaymentTransRecordsActivity
    )

    var vm: QueryGoodsOrderPageVm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        mGoodsId = intent.getStringExtra("goodsId")

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
    }

    override fun onResume() {
        super.onResume()
        binding.srlList.autoRefresh()
    }

    class QueryGoodsOrderPageVm : PagedVm<QueryGoodsOrderPageBean>() {

        override fun loadPage(page: Int): Single<PagedList<QueryGoodsOrderPageBean>> {

            return GardenOperations.refreshToken {
                App.get().marketingApi.queryGoodsOrderPage(it, mGoodsId, page, 20).check()
            }
        }
    }

    companion object {
        var mGoodsId: String = ""
    }

}
