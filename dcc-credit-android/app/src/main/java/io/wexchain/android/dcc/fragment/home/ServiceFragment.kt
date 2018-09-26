package io.wexchain.android.dcc.fragment.home

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.jcodecraeer.xrecyclerview.CustomFooterViewCallBack
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.*
import io.wexchain.android.dcc.base.BindFragment
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.bsx.BsxMarketActivity
import io.wexchain.android.dcc.modules.home.LoanActivity
import io.wexchain.android.dcc.modules.home.TokenPlusActivity
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.BindingViewHolder
import io.wexchain.android.dcc.view.adapter.ClickAwareHolder
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentServiceBinding
import io.wexchain.dcc.databinding.ItemServiceLoanBinding
import io.wexchain.dccchainservice.domain.LoanProduct
import kotlinx.android.synthetic.main.fragment_service.*

/**
 *Created by liuyang on 2018/9/18.
 */
class ServiceFragment : BindFragment<FragmentServiceBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.fragment_service

    private val passport by lazy {
        App.get().passportRepository.getCurrentPassport()
    }

    private val adapter = Adapter(this::onItemClick)

    private val header by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        LayoutInflater.from(activity).inflate(R.layout.service_header, service_toot, false)
    }
    private val foot by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        LayoutInflater.from(activity).inflate(R.layout.service_foot, service_toot, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
        initClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvList.destroy()
    }

    private fun initClick() {
        header.findViewById<Button>(R.id.btn_borrow).onClick {
            navigateTo(LoanActivity::class.java)
        }
        header.findViewById<TextView>(R.id.home_credit_more).onClick {
            navigateTo(LoanActivity::class.java)
        }

        header.findViewById<View>(R.id.card_passport).setOnClickListener {
            if (App.get().passportRepository.passportEnabled) {
                navigateTo(MyCreditNewActivity::class.java)
            } else {
                toast(R.string.ca_not_enabled)
            }
        }
        header.findViewById<TextView>(R.id.tv_passport_address).onClick {
            navigateTo(PassportAddressActivity::class.java)
        }
        foot.findViewById<RelativeLayout>(R.id.home_assets).onClick {
            navigateTo(TokenPlusActivity::class.java)
        }
        foot.findViewById<RelativeLayout>(R.id.home_login).onClick {
            navigateTo(PassportActivity::class.java)
        }
        foot.findViewById<Button>(R.id.btn_bsx).onClick {
            navigateTo(BsxMarketActivity::class.java)
        }

    }

    private fun initView() {
        adapter.lifecycleOwner = this
        binding.rvList.run {
            adapter = this@ServiceFragment.adapter
            layoutManager = GridLayoutManager(activity, 2)
            setPullRefreshEnabled(false)
            addHeaderView(header)
            setFootView(foot, object : CustomFooterViewCallBack {
                override fun onSetNoMore(yourFooterView: View?, noMore: Boolean) {

                }

                override fun onLoadingMore(yourFooterView: View?) {

                }

                override fun onLoadMoreComplete(yourFooterView: View?) {

                }
            })
        }
        header.findViewById<TextView>(R.id.tv_passport_address).text = ViewModelHelper.maskAddress2(passport?.address)
    }

    fun onItemClick(position: Int, viewId: Int) {
        adapter.getItemOnPos(position - 2).let {
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
            DataBindAdapter<ItemServiceLoanBinding, LoanProduct>(
                    R.layout.item_service_loan,
                    itemDiffCallback = defaultItemDiffCallback()
            ) {
        override fun bindData(binding: ItemServiceLoanBinding, item: LoanProduct?) {
            binding.product = item
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ItemServiceLoanBinding> {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemServiceLoanBinding = DataBindingUtil.inflate(layoutInflater, layout, parent, false)
            return ClickAwareHolder(binding, onPosClick)
        }
    }


}
