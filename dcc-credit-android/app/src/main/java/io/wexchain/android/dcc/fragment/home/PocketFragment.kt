package io.wexchain.android.dcc.fragment.home

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.view.View
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.modules.bsx.BsxHoldingActivity
import io.wexchain.android.dcc.modules.digestpocket.DigestPocketHomeActivity
import io.wexchain.android.dcc.modules.digital.DigitalCurrencyActivity
import io.wexchain.android.dcc.modules.other.QrScannerPocketActivity
import io.wexchain.android.dcc.modules.trans.activity.DccExchangeActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketHomeActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketOpenTipActivity
import io.wexchain.android.dcc.tools.StringUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.DigitalAssetsAdapter
import io.wexchain.android.dcc.vm.DigitalAssetsVm
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.android.dcc.vm.currencyToDisplayStr
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentPocketBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal
import java.math.RoundingMode

/**
 *Created by liuyang on 2018/9/27.
 */
class PocketFragment : BindFragment<FragmentPocketBinding>(), ItemViewClickListener<DigitalCurrency> {

    private var tmpList: List<DigitalCurrency>? = null
    private val adapter = DigitalAssetsAdapter(this)

    override val contentLayoutId: Int get() = R.layout.fragment_pocket

    var isOpenTrustPocket: Boolean = false


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val assetsVm = getViewModel<DigitalAssetsVm>()
        assetsVm.ensureHolderAddress(this)
        assetsVm.assets.observe(this, Observer {
            tmpList = it
            adapter.setList(it)
        })
        assetsVm.assetsFilter.set(false)
        assetsVm.filterEvent.observe(this, Observer {
            val b = assetsVm.assetsFilter.get()!!
            assetsVm.assetsFilter.set(!b)
            if (b) {
                adapter.setList(tmpList)
            } else {
                val tmp = mutableListOf<DigitalCurrency>()
                tmpList!!.forEach {
                    val hoding = assetsVm.holding[it]
                    val balanceStr = ViewModelHelper.getBalanceStr(it, hoding)
                    if (balanceStr != "--" && balanceStr.isNotEmpty()) {
                        val value = BigDecimal(balanceStr)
                        if (value.compareTo(BigDecimal.ZERO) != 0) {
                            tmp.add(it)
                        }
                    }
                }
                adapter.setList(tmp)
            }
        })

        binding.assets = assetsVm
        adapter.assetsVm = assetsVm
        binding.rvAssets.adapter = adapter


        Singles.zip(
                GardenOperations.refreshToken {
                    App.get().marketingApi.getHostingWallet(it).check()
                },
                GardenOperations.refreshToken {
                    App.get().marketingApi.getAssetOverview(it).check()
                },

                App.get().scfApi.getHoldingSum(App.get().passportRepository.currPassport.value!!.address).check())
                .doMain()
                .withLoading()
                .subscribeBy(onSuccess = {

                    val trustAmount = it.second.totalPrice.amount.toBigDecimal().multiply(App.get().mUsdtquote.toBigDecimal())

                    binding.tvTrustAmount.text = "≈￥" + trustAmount.currencyToDisplayStr()

                    val bsxAccount = if (null == it.third.corpus) {
                        "0"
                    } else StringUtils.keep4double(it.third.corpus)

                    binding.tvBsx.text = "≈￥$bsxAccount"

                    val digestAccountRnb = binding.tvDigestRnb.text.toString()

                    var res = BigDecimal.ZERO

                    if ("" == digestAccountRnb) {
                        res = BigDecimal.ZERO
                    } else {
                        res = digestAccountRnb.toBigDecimal()
                    }

                    binding.assetsAmountValue.text = "￥" + trustAmount.plus(bsxAccount.toBigDecimal()).plus(res).currencyToDisplayStr()
                    binding.tvTotalUsdt.text = "≈" + trustAmount.plus(bsxAccount.toBigDecimal()).plus(res).divide(App.get().mUsdtquote.toBigDecimal(), 4, RoundingMode.DOWN).currencyToDisplayStr() + " USDT"


                    // 已开户
                    if (it.first.mobileUserId != null) {
                        isOpenTrustPocket = true
                        binding.ivNext.visibility = View.VISIBLE
                        binding.btOpen.visibility = View.GONE
                        App.get().mobileUserId = it.second.mobileUserId

                    } else {
                        isOpenTrustPocket = false
                        binding.ivNext.visibility = View.GONE
                        binding.btOpen.visibility = View.VISIBLE
                    }

                }, onError = {
                    // 未开户
                    isOpenTrustPocket = false
                    binding.ivNext.visibility = View.GONE
                    binding.btOpen.visibility = View.VISIBLE
                })

        binding.assets!!.updateHoldingAndQuote()

        binding.rlTrustPocket.onClick {

            if (isOpenTrustPocket) {
                navigateTo(TrustPocketHomeActivity::class.java)
            } else {
                navigateTo(TrustPocketOpenTipActivity::class.java)
            }
        }

        binding.btOpen.onClick {
            navigateTo(TrustPocketOpenTipActivity::class.java)
        }

        binding.rlDigestPocket.onClick {
            navigateTo(DigestPocketHomeActivity::class.java)
        }

        binding.rlInvest.onClick {
            navigateTo(BsxHoldingActivity::class.java)
        }

        binding.ivScan.onClick {
            navigateTo(QrScannerPocketActivity::class.java)
        }

    }

    override fun onItemClick(item: DigitalCurrency?, position: Int, viewId: Int) {
        item ?: return
        when (item.chain) {
            Chain.MultiChain -> {
                if (item.symbol == Currencies.DCC.symbol) {
                    navigateTo(DccExchangeActivity::class.java)
                }
            }
            else -> {
                navigateTo(DigitalCurrencyActivity::class.java) {
                    putExtra(Extras.EXTRA_DIGITAL_CURRENCY, item)
                    putExtra(Extras.EXTRA_DC_SELECTED, true)
                }
            }
        }
    }

}
