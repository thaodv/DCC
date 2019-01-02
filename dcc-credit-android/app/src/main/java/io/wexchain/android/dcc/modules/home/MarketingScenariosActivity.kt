package io.wexchain.android.dcc.modules.home

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.modules.cert.MyCreditActivity
import io.wexchain.android.dcc.network.GlideApp
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemMarketingScenarioBinding
import io.wexchain.dccchainservice.domain.MarketingActivityScenario
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.ipfs.utils.doMain

class MarketingScenariosActivity : BaseCompatActivity(), ItemViewClickListener<MarketingActivityScenario> {

    private val passport: Passport by lazy {
        App.get().passportRepository.getCurrentPassport()!!
    }

    private val adapter = SimpleDataBindAdapter<ItemMarketingScenarioBinding, MarketingActivityScenario>(
            layoutId = R.layout.item_marketing_scenario,
            variableId = BR.scene,
            itemViewClickListener = this@MarketingScenariosActivity,
            clickAwareViewIds = *intArrayOf(R.id.btn_action)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketing_scenarios)
        initToolbar()
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadScenarios()
    }

    override fun onItemClick(item: MarketingActivityScenario?, position: Int, viewId: Int) {
        item?.let {
            when (it.qualification) {
                MarketingActivityScenario.Qualification.REDEEMED -> {
                    //no op
                }
                MarketingActivityScenario.Qualification.AVAILABLE -> {
                    redeemScenario(it.code, passport.address)
                }
                else -> {
                    // cert undone
                    navigateTo(MyCreditActivity::class.java)
                }
            }
        }
    }

    private fun redeemScenario(code: String, address: String) {
        App.get().marketingApi.applyRedeemToken(code, address)
                .compose(Result.checkedAllowingNull(""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    loadScenarios()
                }
    }

    private fun loadScenarios() {
        App.get().marketingApi.queryActivity()
                .check()
                .toObservable()
                .flatMap {
                    Observable.fromIterable(it)
                }
                .filter {
                    "10001" == it.code
                }
                .map {
                    val data = App.get().marketingApi.queryScenario(it.code, passport.address).check().blockingGet()
                    val url = it.bannerImgUrl
                    data to url
                }
                .doMain()
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .doOnError {
                    toast(R.string.common_service_error_toast)
                }
                .subscribeBy {
                    adapter.setList(it.first)
                    setBanner(it.second)
                }
    }

    private fun setBanner(bannerImgUrl: String?) {
        val ivBanner = findViewById<ImageView>(R.id.iv_banner)
        ivBanner.visibility = if (bannerImgUrl == null) View.GONE else View.VISIBLE
        bannerImgUrl?.let {
            GlideApp.with(ivBanner)
                    .load(it)
                    .into(ivBanner)
        }
    }
}