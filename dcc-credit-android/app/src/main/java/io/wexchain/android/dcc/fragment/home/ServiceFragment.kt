package io.wexchain.android.dcc.fragment.home

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.jcodecraeer.xrecyclerview.CustomFooterViewCallBack
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.AuthManageActivity
import io.wexchain.android.dcc.LoanProductDetailActivity
import io.wexchain.android.dcc.base.BaseCompatFragment
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.bsx.BsxMarketActivity
import io.wexchain.android.dcc.modules.home.TokenPlusActivity
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.BindingViewHolder
import io.wexchain.android.dcc.view.adapter.ClickAwareHolder
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemLoanProductBinding
import io.wexchain.dccchainservice.domain.LoanProduct
import kotlinx.android.synthetic.main.fragment_service.*

/**
 *Created by liuyang on 2018/9/18.
 */
class ServiceFragment : BaseCompatFragment() {

    private val adapter = Adapter(this::onItemClick)
    private val header by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        LayoutInflater.from(activity).inflate(R.layout.service_header, service_toot, false)
    }
    private val foot by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        LayoutInflater.from(activity).inflate(R.layout.service_foot, service_toot, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
        initClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        rv_list?.destroy()
    }

    private fun initClick() {
        foot.findViewById<RelativeLayout>(R.id.home_assets).onClick {
            activity?.navigateTo(TokenPlusActivity::class.java)
        }
        foot.findViewById<RelativeLayout>(R.id.home_login).onClick {
            activity?.navigateTo(AuthManageActivity::class.java)
        }
        foot.findViewById<RelativeLayout>(R.id.home_bsx).onClick {
            activity?.navigateTo(BsxMarketActivity::class.java)
        }
    }

    private fun initView() {
        adapter.lifecycleOwner = this
        rv_list.adapter = adapter
        rv_list.layoutManager = GridLayoutManager(activity, 2)
        rv_list.setPullRefreshEnabled(false)
        rv_list.addHeaderView(header)
        rv_list.setFootView(foot, object : CustomFooterViewCallBack {
            override fun onSetNoMore(yourFooterView: View?, noMore: Boolean) {

            }

            override fun onLoadingMore(yourFooterView: View?) {

            }

            override fun onLoadMoreComplete(yourFooterView: View?) {

            }
        })
    }

    fun onItemClick(position: Int, viewId: Int) {
        adapter.getItemOnPos(position).let {
            activity?.navigateTo(LoanProductDetailActivity::class.java) {
                putExtra(Extras.EXTRA_LOAN_PRODUCT_ID, it.id)
            }
        }
    }

    private fun loadData() {
        App.get().scfApi.queryLoanProductsByLenderCode()
                .checkonMain()
                .subscribeBy {
                    adapter.setList(it)
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
