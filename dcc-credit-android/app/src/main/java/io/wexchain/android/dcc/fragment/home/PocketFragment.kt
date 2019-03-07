package io.wexchain.android.dcc.fragment.home

import android.arch.lifecycle.Observer
import android.view.View
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.modules.bsx.BsxHoldingActivity
import io.wexchain.android.dcc.modules.digestpocket.DigestPocketHomeActivity
import io.wexchain.android.dcc.modules.digital.DigitalCurrencyActivity
import io.wexchain.android.dcc.modules.trans.activity.DccExchangeActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketHomeActivity
import io.wexchain.android.dcc.modules.trustpocket.TrustPocketOpenTipActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.DigitalAssetsAdapter
import io.wexchain.android.dcc.vm.DigitalAssetsVm
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentPocketBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal

/**
 *Created by liuyang on 2018/9/27.
 */
class PocketFragment : BindFragment<FragmentPocketBinding>(), ItemViewClickListener<DigitalCurrency> {

    private var tmpList: List<DigitalCurrency>? = null
    private val adapter = DigitalAssetsAdapter(this)

    override val contentLayoutId: Int get() = R.layout.fragment_pocket

    var isOpenTrustPocket:Boolean = false

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

        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getHostingWallet(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    // 已开户
                    if (it?.mobileUserId != null){
                        isOpenTrustPocket = true
                        binding.ivNext.visibility= View.VISIBLE
                        binding.btOpen.visibility = View.GONE
                    }else{
                        isOpenTrustPocket = false
                        binding.ivNext.visibility= View.GONE
                        binding.btOpen.visibility = View.VISIBLE
                    }
                }, {
                    // 未开户
                    isOpenTrustPocket = false
                    binding.ivNext.visibility= View.GONE
                    binding.btOpen.visibility = View.VISIBLE
                })



        binding.assets!!.updateHoldingAndQuote()

        binding.rlTrustPocket.onClick {

            if(isOpenTrustPocket){
                navigateTo(TrustPocketHomeActivity::class.java)
            }else{
                navigateTo(TrustPocketOpenTipActivity::class.java)
            }
        }

        binding.rlDigestPocket.onClick {
            navigateTo(DigestPocketHomeActivity::class.java)
        }

        binding.rlInvest.onClick {
            navigateTo(BsxHoldingActivity::class.java)
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
