package io.wexchain.android.dcc.fragment.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.*
import io.wexchain.android.dcc.base.StaticHtmlActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.bsx.BsxMarketActivity
import io.wexchain.android.dcc.modules.home.LoanActivity
import io.wexchain.android.dcc.modules.home.TokenPlusActivity
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.BindingViewHolder
import io.wexchain.android.dcc.view.adapter.ClickAwareHolder
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.android.dcc.vm.AuthenticationStatusVm
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

    override fun onResume() {
        super.onResume()
        initVm()
    }

    private fun getCardVm(type: CardType): ServiceCardVm {
        return ViewModelProviders.of(this)[type.name, ServiceCardVm::class.java].apply {
            name.set(getName(type))
            title.set(getTitle(type))
            message.set(getMessage(type))
            img.set(getImg(type))
            performOperationEvent.observe(this@ServiceFragment, Observer {
                cardClick(type)
            })
        }
    }

    private fun cardClick(type: CardType) {
        when (type) {
            CardType.TOKENPLUS -> {
                navigateTo(TokenPlusActivity::class.java)
            }
            CardType.SEARCHAIN -> {
                startActivity(StaticHtmlActivity.getResultIntent(activity!!, "Searchain数据分析", Extras.Searchain_BASE))
            }
            CardType.ECOLOGY -> {
                navigateTo(DccEcoRewardsActivity::class.java)
            }
            CardType.CERTIFICATION -> {
                navigateTo(MarketingScenariosActivity::class.java)
            }
            CardType.LOGIN -> {
                navigateTo(PassportActivity::class.java)
            }
        }
    }

    private fun getImg(type: CardType): Drawable? {
        return when (type) {
            CardType.TOKENPLUS -> ContextCompat.getDrawable(activity!!, R.drawable.service_tokenplus)
            CardType.SEARCHAIN -> ContextCompat.getDrawable(activity!!, R.drawable.service_searchain)
            CardType.ECOLOGY -> ContextCompat.getDrawable(activity!!, R.drawable.service_ecology)
            CardType.CERTIFICATION -> ContextCompat.getDrawable(activity!!, R.drawable.service_certification)
            CardType.LOGIN -> ContextCompat.getDrawable(activity!!, R.drawable.service_login)
        }
    }

    private fun getMessage(type: CardType): String? {
        return when (type) {
            CardType.TOKENPLUS -> "专为数字货币持有者提供高效的套利服务"
            CardType.SEARCHAIN -> "为用户提供数字资产网络的交易监测服务"
            CardType.ECOLOGY -> "对DCC链上数据做出贡献的奖励！"
            CardType.CERTIFICATION -> "完成BitExpress认证 领取DCC奖励"
            CardType.LOGIN -> "使用个人数字认证信息实现联合登录！"
        }
    }

    private fun getTitle(type: CardType): String? {
        return when (type) {
            CardType.TOKENPLUS -> "TokenPlus套利神器"
            CardType.SEARCHAIN -> "数字资产追踪引擎"
            CardType.ECOLOGY -> "DCC奖励"
            CardType.CERTIFICATION -> "认证有奖励"
            CardType.LOGIN -> "扫码登录"
        }
    }

    private fun getName(type: CardType): String {
        return when (type) {
            CardType.TOKENPLUS -> "资产套利"
            CardType.SEARCHAIN -> "Searchain资产分析"
            CardType.ECOLOGY -> "生态奖励"
            CardType.CERTIFICATION -> "认证奖励"
            CardType.LOGIN -> "统一登录管理"
        }
    }

    private fun initVm() {
        binding.tokenplus = getCardVm(CardType.TOKENPLUS)
        binding.certification = getCardVm(CardType.CERTIFICATION)
        binding.ecology = getCardVm(CardType.ECOLOGY)
        binding.searchain = getCardVm(CardType.SEARCHAIN)
        binding.login = getCardVm(CardType.LOGIN)
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
        binding.viewCardPassport.cardPassport.onClick {
            navigateTo(MyCreditNewActivity::class.java)
        }
        binding.viewCardPassport.tvPassportAddress.onClick {
            navigateTo(PassportAddressActivity::class.java)
        }
       binding.serviceMail.onClick {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.bitexpress_info_mail_address)))
            startActivity(Intent.createChooser(intent, "发送邮件"))
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
