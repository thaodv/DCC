package io.wexchain.android.dcc.fragment.home

import android.arch.lifecycle.Observer
import com.wexmarket.android.network.Networking
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.modules.digital.DigitalCurrencyActivity
import io.wexchain.android.dcc.modules.digital.SearchDigitalCurrencyActivity
import io.wexchain.android.dcc.modules.selectnode.SelectNodeActivity
import io.wexchain.android.dcc.modules.trans.activity.DccExchangeActivity
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.DigitalAssetsAdapter
import io.wexchain.android.dcc.vm.DigitalAssetsVm
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDigitalAssetsBinding
import io.wexchain.digitalwallet.Chain
import io.wexchain.digitalwallet.Currencies
import io.wexchain.digitalwallet.DigitalCurrency
import io.wexchain.digitalwallet.api.EthJsonRpcApiWithAuth
import io.wexchain.digitalwallet.proxy.EthsRpcAgent
import io.wexchain.ipfs.utils.io_main
import java.math.BigDecimal

/**
 *Created by liuyang on 2018/9/27.
 */
class DigitalAssetsFragment : BindFragment<ActivityDigitalAssetsBinding>(), ItemViewClickListener<DigitalCurrency> {

    private var tmpList: List<DigitalCurrency>? = null
    private val adapter = DigitalAssetsAdapter(this)

    override val contentLayoutId: Int get() = R.layout.activity_digital_assets

    override fun onResume() {
        super.onResume()

        var base = ShareUtils.getString(Extras.SP_SELECTED_NODE, App.get().nodeList[0].url)

        if (base == "https://ethrpc4.wexfin.com:58545/") {
            base = App.get().nodeList[0].url
        }

        val createApi = Networking(App.get()).createApi(EthJsonRpcApiWithAuth::class.java, base)
        val agent = EthsRpcAgent.by(createApi)

        Single.just(System.currentTimeMillis())
                .zipWith(agent.checkNode())
                .map {
                    when {
                        System.currentTimeMillis() - it.first <= 300 -> R.drawable.img_node_green
                        System.currentTimeMillis() - it.first <= 1000 -> R.drawable.img_node_yellow
                        else -> R.drawable.img_node_red
                    }
                }
                .onErrorReturn {
                    R.drawable.img_node_red
                }
                .io_main()
                .doOnSuccess {
                    binding.digitalSelectNode.setImageResource(it)
                }
                .subscribe()

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

        binding.assets!!.updateHoldingAndQuote()

        binding.digitalAdd.onClick {
            navigateTo(SearchDigitalCurrencyActivity::class.java)
        }
        binding.digitalSelectNode.onClick {
            navigateTo(SelectNodeActivity::class.java)
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
