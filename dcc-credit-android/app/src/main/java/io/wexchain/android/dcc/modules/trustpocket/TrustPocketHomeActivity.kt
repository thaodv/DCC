package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.LogUtils
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.android.dcc.vm.currencyToDisplayRMBStr
import io.wexchain.android.dcc.vm.currencyToDisplayStr
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustPocketHomeBinding
import io.wexchain.dcc.databinding.ItemTrustPocketHomeBinding
import io.wexchain.dccchainservice.domain.trustpocket.ResultAssetBean
import io.wexchain.ipfs.utils.doMain
import java.math.RoundingMode
import java.util.*

class TrustPocketHomeActivity : BindActivity<ActivityTrustPocketHomeBinding>(), ItemViewClickListener<ResultAssetBean> {

    override val contentLayoutId: Int get() = R.layout.activity_trust_pocket_home

    var resultAsset: MutableList<ResultAssetBean> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        getConinData()

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
                    binding.totalPrice2 = "≈" + it.first.totalPrice.amount.toBigDecimal().setScale(8, RoundingMode.DOWN) + " " + it.first.totalPrice.assetCode

                    binding.totalPrice = "≈￥" + it.first.totalPrice.amount.toBigDecimal().multiply(App.get().mUsdtquote.toBigDecimal()).currencyToDisplayRMBStr()

                    val holdingList = it.first.assetList

                    val assetList = it.second

                    var res: ResultAssetBean
                    if (null == holdingList || holdingList.isEmpty()) {
                        for (item in assetList) {
                            res = ResultAssetBean(item.url, item.cryptoAssetConfig.code, item.cryptoAssetConfig.name, "0.000", "--")
                            resultAsset.add(res)
                        }
                    } else {
                        for (item in assetList) {
                            for (it in holdingList) {

                                if (it.assetValue.assetCode == item.cryptoAssetConfig.code) {
                                    res = ResultAssetBean(item.url, item.cryptoAssetConfig.code, item.cryptoAssetConfig.name, it.assetValue.amount.toBigDecimal().currencyToDisplayStr(), "≈￥" + it.legalTenderPrice.amount.toBigDecimal().multiply(App.get().mUsdtquote.toBigDecimal()).toPlainString().toBigDecimal().currencyToDisplayStr())
                                } else {
                                    res = ResultAssetBean(item.url, item.cryptoAssetConfig.code, item.cryptoAssetConfig.name, "0.000", "--")
                                }
                                resultAsset.add(res)
                            }
                        }
                    }


                    LogUtils.i("res:", resultAsset.toString())

                    val adapter = ResultAssetAdapter(this)
                    adapter.setList(resultAsset)
                    binding.rvAssets.adapter = adapter

                }, onError = {

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

        override fun bindData(binding: ItemTrustPocketHomeBinding, item: ResultAssetBean?) {
            binding.res = item
        }
    }


}
