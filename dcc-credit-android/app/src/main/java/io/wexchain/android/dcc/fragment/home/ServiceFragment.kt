package io.wexchain.android.dcc.fragment.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.fragment.home.vm.ServiceCardVm
import io.wexchain.android.dcc.modules.bsx.BsxMarketActivity
import io.wexchain.android.dcc.modules.cert.MyCreditActivity
import io.wexchain.android.dcc.modules.home.DccEcoRewardsActivity
import io.wexchain.android.dcc.modules.home.MarketingScenariosActivity
import io.wexchain.android.dcc.modules.home.TokenPlusActivity
import io.wexchain.android.dcc.modules.loan.LoanActivity
import io.wexchain.android.dcc.modules.loan.LoanProductDetailActivity
import io.wexchain.android.dcc.modules.other.StaticHtmlActivity
import io.wexchain.android.dcc.modules.passport.PassportActivity
import io.wexchain.android.dcc.modules.passport.PassportAddressActivity
import io.wexchain.android.dcc.modules.paymentcode.PaymentUnOpenActivity
import io.wexchain.android.dcc.modules.paymentcode.RepaymentQuickReceiptActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketOpenTipActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.android.dcc.view.adapter.BindingViewHolder
import io.wexchain.android.dcc.view.adapter.ClickAwareHolder
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.defaultItemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentServiceBinding
import io.wexchain.dcc.databinding.ItemServiceLoanBinding
import io.wexchain.dccchainservice.domain.LoanProduct
import io.wexchain.ipfs.utils.doMain
import io.wexchain.ipfs.utils.io_main


/**
 *Created by liuyang on 2018/9/18.
 */
class ServiceFragment : BindFragment<FragmentServiceBinding>() {

    override val contentLayoutId: Int get() = R.layout.fragment_service

    var isOpenTrustPocket: Boolean = false
    var isBindTelegram: Boolean = false

    private val passport by lazy {
        App.get().passportRepository.getCurrentPassport()
    }

    private val adapter = Adapter(this::onItemClick)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initVm()
        loadData()
        initClick()
    }

    override fun onResume() {
        super.onResume()
        getUsdtCnyQuote()
        getHostingWallet()
        getTelegramUser()
    }

    private fun getUsdtCnyQuote() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getUsdtCnyQuote(it)
                }
                .doMain()
                .withLoading()
                .subscribe({
                    App.get().mUsdtquote = it.result.toString()
                }, {
                    // toast(it.message.toString())
                })
    }

    private fun getCardVm(type: CardType): ServiceCardVm {
        return ViewModelProviders.of(this)[type.name, ServiceCardVm::class.java].apply {
            name.set(getName(type))
            title.set(getTitle(type))
            message.set(getMessage(type))
            img.set(getImg(type))
            showBtn.set(isShow(type))
            showTip.set(isShowTip(type))
            btnTxt.set(getBtnTxt(type))
            performOperationEvent.observe(this@ServiceFragment, Observer {
                cardClick(type)
            })
        }
    }

    private fun getHostingWallet() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getHostingWallet(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    // 已开户
                    if (it?.mobileUserId != null) {
                        isOpenTrustPocket = true
                        App.get().mobileUserId = it.mobileUserId
                    } else {
                        isOpenTrustPocket = false
                    }
                }, {
                    // 未开户
                    isOpenTrustPocket = false
                })
    }

    private fun getTelegramUser() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getTelegramUser(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    isBindTelegram = true
                }, {
                    isBindTelegram = false
                })
    }

    private fun getBtnTxt(type: CardType): String {
        return when (type) {
            CardType.BSX -> "去认购"
            else -> ""
        }
    }

    private fun isShow(type: CardType): Boolean {
        return when (type) {
            CardType.BSX -> true
            else -> false
        }
    }

    private fun isShowTip(type: CardType): Boolean {
        return when (type) {
            CardType.BSX -> true
            else -> false
        }
    }

    private fun cardClick(type: CardType) {
        when (type) {
            CardType.PAYMENT -> {

                val sp = activity!!.getSharedPreferences("setting", Context.MODE_PRIVATE)
                if (sp.getBoolean("payment_first_into", true)) {
                    if (isOpenTrustPocket) {
                        navigateTo(PaymentUnOpenActivity::class.java)
                    } else {
                        navigateTo(TrustPocketOpenTipActivity::class.java)
                    }
                } else {
                    if (isOpenTrustPocket) {
                        navigateTo(RepaymentQuickReceiptActivity::class.java)
                    } else {
                        navigateTo(TrustPocketOpenTipActivity::class.java)
                    }
                }
            }
            CardType.BSX -> {
                navigateTo(BsxMarketActivity::class.java)
            }
            CardType.TOKENPLUS -> {
                navigateTo(TokenPlusActivity::class.java)
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
            CardType.PAYMENT -> ContextCompat.getDrawable(activity!!, R.drawable.icon_payment_enter)
            CardType.BSX -> ContextCompat.getDrawable(activity!!, R.drawable.service_bsx)
            CardType.TOKENPLUS -> ContextCompat.getDrawable(activity!!, R.drawable.service_tokenplus)
            CardType.ECOLOGY -> ContextCompat.getDrawable(activity!!, R.drawable.service_ecology)
            CardType.CERTIFICATION -> ContextCompat.getDrawable(activity!!, R.drawable.service_certification)
            CardType.LOGIN -> ContextCompat.getDrawable(activity!!, R.drawable.service_login)
        }
    }

    private fun getMessage(type: CardType): String? {
        return when (type) {
            CardType.PAYMENT -> "无需开发、易操作、方便快捷"
            CardType.BSX -> "极低风险跨市套利让你持币有红利"
            CardType.TOKENPLUS -> "专为数字货币持有者提供高效的套利服务"
            CardType.ECOLOGY -> "对DCC链上数据做出贡献的奖励！"
            CardType.CERTIFICATION -> "完成BitExpress认证 领取DCC奖励"
            CardType.LOGIN -> "使用个人数字认证信息实现联合登录！"
        }
    }

    private fun getTitle(type: CardType): String? {
        return when (type) {
            CardType.PAYMENT -> "你最方便的收钱工具"
            CardType.BSX -> getString(R.string.service_bsx_title)
            CardType.TOKENPLUS -> "TokenPlus套利神器"
            CardType.ECOLOGY -> "DCC奖励"
            CardType.CERTIFICATION -> "认证有奖励"
            CardType.LOGIN -> "扫码登录"
        }
    }

    private fun getName(type: CardType): String {
        return when (type) {
            CardType.PAYMENT -> "收款码服务"
            CardType.BSX -> "币生息"
            CardType.TOKENPLUS -> "资产套利"
            CardType.ECOLOGY -> "生态奖励"
            CardType.CERTIFICATION -> "认证奖励"
            CardType.LOGIN -> "统一登录管理"
        }
    }

    private fun initVm() {
        binding.viewCardPassport.passport = passport
        binding.payment = getCardVm(CardType.PAYMENT)
        binding.bsx = getCardVm(CardType.BSX)
        binding.tokenplus = getCardVm(CardType.TOKENPLUS)
        binding.certification = getCardVm(CardType.CERTIFICATION)
        binding.ecology = getCardVm(CardType.ECOLOGY)
        binding.login = getCardVm(CardType.LOGIN)
    }

    private fun initClick() {
        binding.btnBorrow.onClick {
            navigateTo(LoanActivity::class.java)
        }
        binding.homeCreditMore.onClick {
            navigateTo(LoanActivity::class.java)
        }
        binding.viewCardPassport.cardPassport.onClick {
            navigateTo(MyCreditActivity::class.java)
        }
        binding.viewCardPassport.tvPassportAddress.onClick {
            navigateTo(PassportAddressActivity::class.java)
        }
        /*binding.serviceMail.onClick {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.bitexpress_info_mail_address)))
            startActivity(Intent.createChooser(intent, "发送邮件"))
        }*/

        binding.rlOpen.onClick {

            if (isOpenTrustPocket && isBindTelegram) {
                val packageManager = context!!.packageManager
                val intent: Intent? = packageManager.getLaunchIntentForPackage("org.telegram.messenger")
                if (intent == null) {
                    toast("未安装")
                } else {
                    startActivity(intent)
                }
            } else {

                val url = /*if (BuildConfig.DEBUG) "http://func.bitexpress.io/hosting-wallet-website/lottery.html#/launchWay?env=BitExpress" else*/ "http://www.bitexpress.io/hosting-wallet-website/lottery.html#/launchWay?env=BitExpress"
                startActivity(StaticHtmlActivity.getResultIntent(context, "如何发起", url))
            }
        }

        binding.rlParticipate.onClick {
            if (isOpenTrustPocket && isBindTelegram) {
                val packageManager = context!!.packageManager
                val intent: Intent? = packageManager.getLaunchIntentForPackage("org.telegram.messenger")
                if (intent == null) {
                    toast("未安装")
                } else {
                    startActivity(intent)
                }
            } else {
                val url = /*if (BuildConfig.DEBUG) "http://func.bitexpress.io/hosting-wallet-website/lottery.html#/participateWay?env=BitExpress" else*/ "http://www.bitexpress.io/hosting-wallet-website/lottery.html#/participateWay?env=BitExpress"
                startActivity(StaticHtmlActivity.getResultIntent(context, "如何参与", url))
            }
        }

    }

    private fun initView() {
        adapter.lifecycleOwner = this
        binding.rvList.apply {
            adapter = this@ServiceFragment.adapter
            layoutManager = GridLayoutManager(activity, 2)
            isNestedScrollingEnabled = false
        }
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
        GardenOperations.loginWithCurrentPassport()
                .io_main()
                .withLoading().subscribe()
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
