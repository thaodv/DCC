package io.wexchain.android.dcc.modules.trustpocket

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.common.tools.CommonUtils
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.LogUtils
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.android.dcc.vm.DigitalAssetsVm
import io.wexchain.android.dcc.vm.setSelfScale
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustPocketHomeBinding
import io.wexchain.dcc.databinding.ItemTrustPocketHomeBinding
import io.wexchain.dccchainservice.domain.trustpocket.ResultAssetBean
import io.wexchain.ipfs.utils.doMain
import java.math.BigDecimal
import java.util.*

class TrustPocketHomeActivity : BindActivity<ActivityTrustPocketHomeBinding>(), ItemViewClickListener<ResultAssetBean> {

    override val contentLayoutId: Int get() = R.layout.activity_trust_pocket_home

    var resultAsset: MutableList<ResultAssetBean> = ArrayList()

    val adapter = ResultAssetAdapter(this)

    var mValue1: String = ""
    var mValue2: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        val cacheTrustAmount = ShareUtils.getString(Extras.SP_CACHE_TRUST_AMOUNT, "")
        if ("" != cacheTrustAmount) {
            binding.totalPrice = "≈" + CommonUtils.showCurrencySymbol() + cacheTrustAmount.toBigDecimal().multiply(App.get().mUsdtquote.toBigDecimal()).setSelfScale(2)
            binding.totalPrice2 = "≈" + cacheTrustAmount.toBigDecimal().setSelfScale(8) + " USDT"
        }

        binding.ivBack.onClick {
            finish()
        }

        binding.llIn.onClick {
            navigateTo(TrustChooseCoinActivity::class.java) {
                putExtra("use", "recharge")
            }
        }
        binding.llOut.onClick {
            navigateTo(TrustChooseCoinActivity::class.java) {
                putExtra("use", "withdraw")
            }
        }

        binding.llTrans.onClick {
            /*navigateTo(TrustChooseCoinActivity::class.java) {
                putExtra("use", "transfer")
            }*/
            navigateTo(TrustTransferCheckActivity::class.java)
        }

        binding.llDetail.onClick {
            navigateTo(TrustTradeDetailActivity::class.java)
        }

        binding.ivQuery.onClick {
            navigateTo(TrustChooseCoinActivity::class.java) {
                putExtra("use", "search")
            }
        }

        val assetsVm = getViewModel<DigitalAssetsVm>()
        assetsVm.assetsFilter3.set(false)
        assetsVm.filterEvent3.observe(this, Observer {
            val b = assetsVm.assetsFilter3.get()!!
            assetsVm.assetsFilter3.set(!b)
            if (b) {
                adapter.setList(resultAsset)
            } else {
                val tmp = mutableListOf<ResultAssetBean>()
                resultAsset!!.forEach {
                    if (it.value != "0.0000" && (it.value3.compareTo(BigDecimal.ZERO) != 0)) {
                        tmp.add(it)
                    }
                }
                adapter.setList(tmp)
            }
        })

        assetsVm.assetsFilter4.set(false)
        assetsVm.filterEvent4.observe(this, Observer {
            val b = assetsVm.assetsFilter4.get()!!
            assetsVm.assetsFilter4.set(!b)

            if (b) {
                adapter.isShow = true
                binding.totalPrice = mValue1
                binding.totalPrice2 = mValue2

            } else {
                adapter.isShow = false
                binding.totalPrice = "****"
                binding.totalPrice2 = "****"
            }

            adapter.notifyDataSetChanged()
        })
        binding.assets = assetsVm

    }

    override fun onResume() {
        super.onResume()
        resultAsset.clear()
        getConinData()
    }

    private fun getConinData() {
        Singles.zip(
                GardenOperations.refreshToken {
                    App.get().marketingApi.getAssetOverview(it).check()
                },
                GardenOperations.refreshToken {
                    App.get().marketingApi.listAsset(it).check()
                })
                .doMain()
                .withLoading()
                .subscribeBy(onSuccess = {

                    mValue1 = "≈" + CommonUtils.showCurrencySymbol() + it.first.totalPrice.amount.toBigDecimal().multiply(App.get().mUsdtquote.toBigDecimal()).setSelfScale(2)
                    mValue2 = "≈" + it.first.totalPrice.amount.toBigDecimal().setSelfScale(8) + " " + it.first.totalPrice.assetCode

                    binding.totalPrice = mValue1
                    binding.totalPrice2 = mValue2

                    val holdingList = it.first.assetList

                    val assetList = it.second


                    if (null == holdingList || holdingList.isEmpty()) {
                        for (item in assetList) {
                            val res = ResultAssetBean()
                            res.url = item.url
                            res.code = item.cryptoAssetConfig.code
                            res.name = item.cryptoAssetConfig.name
                            res.value = "0.00000000"
                            res.value2 = "--"
                            res.rank = item.rank

                            resultAsset.add(res)
                        }
                    } else {
                        for (item in assetList) {

                            val tempCode = item.cryptoAssetConfig.code
                            val res = ResultAssetBean()
                            res.url = item.url
                            res.code = tempCode
                            res.name = item.cryptoAssetConfig.name
                            res.rank = item.rank


                            for (it in holdingList) {

                                if (tempCode == it.assetValue.assetCode) {

                                    res.value = it.assetValue.amount.toBigDecimal().setSelfScale(8)
                                    res.value2 = "≈" + CommonUtils.showCurrencySymbol() + it.legalTenderPrice.amount.toBigDecimal().multiply(App.get().mUsdtquote.toBigDecimal()).toPlainString().toBigDecimal().setSelfScale(2)
                                    res.value3 = it.legalTenderPrice.amount.toBigDecimal().multiply(App.get().mUsdtquote.toBigDecimal())
                                }
                                if (resultAsset.contains(res)) {

                                } else {
                                    resultAsset.add(res)
                                }
                            }
                        }
                    }

                    resultAsset.sort()

                    LogUtils.i("resultAsset-res:", resultAsset.toString())

                    adapter.setList(resultAsset)
                    binding.rvAssets.adapter = adapter

                }, onError = {
                    toast(it.message.toString())
                })
    }

    override fun onItemClick(item: ResultAssetBean?, position: Int, viewId: Int) {
        navigateTo(TrustCoinDetailActivity::class.java) {
            putExtra("url", if (null == item!!.url) "" else item.url)
            putExtra("code", item.code)
            putExtra("name", item.name)
            putExtra("value", item.value)
            putExtra("value2", item.value2)
        }
    }

    class ResultAssetAdapter(
            itemViewClickListener: ItemViewClickListener<ResultAssetBean>
    ) : DataBindAdapter<ItemTrustPocketHomeBinding, ResultAssetBean>(
            layout = R.layout.item_trust_pocket_home,
            itemDiffCallback = itemDiffCallback({ a, b -> a.code == b.code }),
            itemViewClickListener = itemViewClickListener
    ) {
        var isShow: Boolean = true

        override fun bindData(binding: ItemTrustPocketHomeBinding, item: ResultAssetBean?) {
            binding.res = item
            binding.isShow = isShow
        }
    }


}
