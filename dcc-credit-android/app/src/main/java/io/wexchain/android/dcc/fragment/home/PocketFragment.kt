package io.wexchain.android.dcc.fragment.home

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import io.reactivex.rxkotlin.Singles
import io.wexchain.android.common.base.BindFragment
import io.wexchain.android.common.constant.Extras
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.tools.CommonUtils
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
import io.wexchain.android.dcc.vm.setSelfScale
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
@SuppressLint("SetTextI18n")
class PocketFragment : BindFragment<FragmentPocketBinding>(), ItemViewClickListener<DigitalCurrency> {

    private var tmpList: List<DigitalCurrency>? = null
    private val adapter = DigitalAssetsAdapter(this)

    override val contentLayoutId: Int get() = R.layout.fragment_pocket

    var isOpenTrustPocket: Boolean = false

    var mValue1: String = ""
    var mValue2: String = ""
    var mTrustValue: String = ""
    var mBsxValue: String = ""

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*val cacheTrustAmount = ShareUtils.getString(Extras.SP_CACHE_TRUST_AMOUNT, "")
        if ("" != cacheTrustAmount) {
            binding.tvTrustAmount.text = "≈" + CommonUtils.showCurrencySymbol() + cacheTrustAmount.toBigDecimal().multiply(App.get().mUsdtquote.toBigDecimal()).setSelfScale(2)
        }

        val cacheBsxAmount = ShareUtils.getString(Extras.SP_CACHE_BSX_AMOUNT, "")
        if ("" != cacheBsxAmount) {
            mBsxValue = "≈" + CommonUtils.showCurrencySymbol() + cacheBsxAmount
        }*/

        /*val cacheDigestAmount = ShareUtils.getString(Extras.SP_CACHE_DIGEST_AMOUNT, "")

        var res = BigDecimal.ZERO

        if ("" == cacheDigestAmount) {
            res = BigDecimal.ZERO
        } else {
            res = cacheDigestAmount.toBigDecimal()
        }*/

    }

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

        assetsVm.assetsFilter5.set(false)
        assetsVm.filterEvent5.observe(this, Observer {
            val b = assetsVm.assetsFilter5.get()!!
            assetsVm.assetsFilter5.set(!b)
            if (b) {

                binding.assetsAmountValue.text = mValue1
                binding.tvTotalUsdt.text = mValue2
                binding.tvTrustAmount.text = mTrustValue
                binding.tvDigestValue.text = assetsVm.assetsSumValue.get()
                binding.tvBsx.text = mBsxValue


            } else {
                binding.assetsAmountValue.text = "****"
                binding.tvTotalUsdt.text = "****"
                binding.tvTrustAmount.text = "****"
                binding.tvDigestValue.text = "****"
                binding.tvBsx.text = "****"

            }
        })

        binding.assets = assetsVm
        adapter.assetsVm = assetsVm
        binding.rvAssets.adapter = adapter


        Singles.zip(
                GardenOperations.refreshToken {
                    App.get().marketingApi.getHostingWallet(it)
                },
                GardenOperations.refreshToken {
                    App.get().marketingApi.getAssetOverview(it).check()
                },

                App.get().scfApi.getHoldingSum(App.get().passportRepository.currPassport.value!!.address).check())
                .doMain()
                .withLoading()
                .subscribe({

                    val tempTrustAmount = it.second.totalPrice.amount

                    //ShareUtils.setString(Extras.SP_CACHE_TRUST_AMOUNT, tempTrustAmount)

                    val trustAmount = tempTrustAmount.toBigDecimal().multiply(App.get().mUsdtquote.toBigDecimal())

                    mTrustValue = "≈" + CommonUtils.showCurrencySymbol() + trustAmount.setSelfScale(2)

                    binding.tvTrustAmount.text = mTrustValue

                    val bsxAccount = if (null == it.third.corpus) {
                        "0"
                    } else StringUtils.keep2double(it.third.corpus)

                    //ShareUtils.setString(Extras.SP_CACHE_BSX_AMOUNT, bsxAccount)

                    mBsxValue = "≈" + CommonUtils.showCurrencySymbol() + bsxAccount

                    binding.tvBsx.text = mBsxValue

                    val digestAccountRnb = binding.tvDigestRnb.text.toString()

                    //ShareUtils.setString(Extras.SP_CACHE_DIGEST_AMOUNT, digestAccountRnb)

                    var res = BigDecimal.ZERO

                    if ("" == digestAccountRnb) {
                        res = BigDecimal.ZERO
                    } else {
                        res = digestAccountRnb.toBigDecimal()
                    }

                    mValue1 = CommonUtils.showCurrencySymbol() + trustAmount.plus(bsxAccount.toBigDecimal()).plus(res).setSelfScale(2)
                    mValue2 = "≈" + trustAmount.plus(bsxAccount.toBigDecimal()).plus(res).divide(App.get().mUsdtquote.toBigDecimal(), 8, RoundingMode.DOWN).setSelfScale(8) + " USDT"

                    binding.assetsAmountValue.text = mValue1
                    binding.tvTotalUsdt.text = mValue2


                    // 已开户
                    if (null == it.first.result) {
                        isOpenTrustPocket = false
                        binding.ivNext.visibility = View.GONE
                        binding.btOpen.visibility = View.VISIBLE
                    } else {
                        if (it.first.result!!.mobileUserId != null) {
                            isOpenTrustPocket = true
                            binding.ivNext.visibility = View.VISIBLE
                            binding.btOpen.visibility = View.GONE
                            App.get().mobileUserId = it.second.mobileUserId

                        } else {
                            isOpenTrustPocket = false
                            binding.ivNext.visibility = View.GONE
                            binding.btOpen.visibility = View.VISIBLE
                        }
                    }

                }, {
                    // 未开户
                    isOpenTrustPocket = false
                    binding.ivNext.visibility = View.GONE
                    binding.btOpen.visibility = View.VISIBLE

                    val digestAccountRnb = binding.tvDigestRnb.text.toString()

                    var res = BigDecimal.ZERO

                    if ("" == digestAccountRnb) {
                        res = BigDecimal.ZERO
                    } else {
                        res = digestAccountRnb.toBigDecimal()
                    }

                    mValue1 = CommonUtils.showCurrencySymbol() + res.setSelfScale(2)
                    mValue2 = "≈" + res.divide(App.get().mUsdtquote.toBigDecimal(), 8, RoundingMode.DOWN).setSelfScale(8) + " USDT"

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
