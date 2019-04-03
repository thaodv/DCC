package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.resultOk
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.ClearEditText
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.android.dcc.vm.setSelfScale
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemTrustCoinQueryBinding
import io.wexchain.dccchainservice.domain.trustpocket.TrustAssetBean
import io.wexchain.ipfs.utils.doMain
import java.math.RoundingMode

class TrustChooseCoinActivity : BaseCompatActivity(), TextWatcher, ItemViewClickListener<TrustAssetBean> {

    private val mUse get() = intent.getStringExtra("use")

    lateinit var mRvList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trust_choose_coin)
        initToolbar()

        mRvList = findViewById(R.id.rv_list)

        loadAssets()
        findViewById<ClearEditText>(R.id.et_search).addTextChangedListener(this)
    }

    private fun loadAssets() {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.listAsset(it).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    val adapter = AssetAdapter(this)
                    adapter.setList(it)
                    mRvList.adapter = adapter
                }, {
                    toast(it.message.toString())
                })
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = s.toString().trim()

        if ("" == text) {
            loadAssets()
        } else {
            GardenOperations
                    .refreshToken {
                        App.get().marketingApi.searchAssetList(it, text).check()
                    }
                    .doMain()
                    .withLoading()
                    .subscribe({
                        val adapter = AssetAdapter(this)
                        adapter.setList(it)
                        mRvList.adapter = adapter
                    }, {
                        toast(it.message.toString())
                    })
        }
    }

    override fun onItemClick(item: TrustAssetBean?, position: Int, viewId: Int) {
        if ("recharge" == mUse) {
            navigateTo(TrustRechargeActivity::class.java) {
                putExtra("code", item!!.cryptoAssetConfig.code)
                putExtra("url", if (null == item.url) "" else item.url)
            }
        } else if ("withdraw" == mUse) {
            navigateTo(TrustWithdrawActivity::class.java) {
                putExtra("code", item!!.cryptoAssetConfig.code)
                putExtra("url", if (null == item.url) "" else item.url)
            }
        } /*else if ("transfer" == mUse) {
            navigateTo(TrustTransferCheckActivity::class.java) {
                putExtra("code", item!!.cryptoAssetConfig.code)
            }
        }*/ else if ("reRecharge" == mUse) {
            resultOk {
                putExtra("code", item!!.cryptoAssetConfig.code)
                putExtra("url", if (null == item.url) "" else item.url)
            }
        } else if ("reWithdraw" == mUse) {
            resultOk {
                putExtra("code", item!!.cryptoAssetConfig.code)
                putExtra("url", if (null == item.url) "" else item.url)
            }
        } else if ("reTransfer" == mUse) {
            resultOk {
                putExtra("code", item!!.cryptoAssetConfig.code)
            }
        } else if ("paymentChoose" == mUse) {
            resultOk {
                putExtra("code", item!!.cryptoAssetConfig.code)
                putExtra("url", if (null == item.url) "" else item.url)
            }
        } else if ("search" == mUse) {
            getBalance(item!!.cryptoAssetConfig.code, if (null == item.url) "" else item.url, item.cryptoAssetConfig.name)
        }
    }

    private fun getBalance(code: String, url: String, name: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getBalance(it, code).check()
                }
                .doMain()
                .subscribe({
                    navigateTo(TrustCoinDetailActivity::class.java) {
                        putExtra("url", url)
                        putExtra("code", code)
                        putExtra("name", name)
                        putExtra("value", it.availableAmount.assetValue.amount.toBigDecimal().setSelfScale(4))
                        putExtra("value2", "≈￥" + it.availableAmount.legalTenderPrice.amount.toBigDecimal().divide(App.get().mUsdtquote.toBigDecimal(), 4, RoundingMode.DOWN).toPlainString())
                    }

                }, {
                    toast(it.message.toString())
                })
    }

    class AssetAdapter(
            itemViewClickListener: ItemViewClickListener<TrustAssetBean>
    ) : DataBindAdapter<ItemTrustCoinQueryBinding, TrustAssetBean>(
            layout = R.layout.item_trust_coin_query,
            itemDiffCallback = itemDiffCallback({ a, b -> a.id == b.id }),
            itemViewClickListener = itemViewClickListener
    ) {

        override fun bindData(binding: ItemTrustCoinQueryBinding, item: TrustAssetBean?) {
            binding.asset = item
        }
    }
}
