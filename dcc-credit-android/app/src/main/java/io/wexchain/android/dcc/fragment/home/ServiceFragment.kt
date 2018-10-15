package io.wexchain.android.dcc.fragment.home

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.*
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.bsx.BsxMarketActivity
import io.wexchain.android.dcc.modules.cashloan.CashLoanActivity
import io.wexchain.android.dcc.modules.home.LoanActivity
import io.wexchain.android.dcc.modules.home.TokenPlusActivity
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.BindingViewHolder
import io.wexchain.android.dcc.view.adapter.ClickAwareHolder
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentServiceBinding
import io.wexchain.dcc.databinding.ItemServiceLoanBinding
import io.wexchain.dccchainservice.domain.LoanProduct

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
        initClick()
    }

    private fun initClick() {
        binding.btnBorrow.onClick {
            navigateTo(LoanActivity::class.java)
        }
        binding.homeCreditMore.onClick {
            navigateTo(LoanActivity::class.java)
        }
        binding.btnBsx.onClick {
            navigateTo(BsxMarketActivity::class.java)
        }
        binding.homeAssets.onClick {
            navigateTo(TokenPlusActivity::class.java)
        }
        binding.homeLogin.onClick {
            navigateTo(PassportActivity::class.java)
        }
        binding.viewCardPassport.cardPassport.onClick {
            navigateTo(MyCreditNewActivity::class.java)
        }
        binding.viewCardPassport.tvPassportAddress.onClick {
            navigateTo(PassportAddressActivity::class.java)
        }
        binding.btnCash.onClick {
            navigateTo(CashLoanActivity::class.java)
        }
    }

    private fun initView() {
        adapter.lifecycleOwner = this
        binding.rvList.run {
            adapter = this@ServiceFragment.adapter
            layoutManager = GridLayoutManager(activity, 2)
            isNestedScrollingEnabled = false
        }
        binding.viewCardPassport.passport = passport
//        binding.viewCardPassport.tvPassportAddress.text = ViewModelHelper.maskAddress2(passport?.address)
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
