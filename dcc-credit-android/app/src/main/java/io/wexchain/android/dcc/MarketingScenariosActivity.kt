package io.wexchain.android.dcc

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.network.GlideApp
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.auth.BR
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ItemMarketingScenarioBinding
import io.wexchain.dccchainservice.domain.MarketingActivity
import io.wexchain.dccchainservice.domain.MarketingActivityScenario
import io.wexchain.dccchainservice.domain.Result

class MarketingScenariosActivity :BaseCompatActivity(), ItemViewClickListener<MarketingActivityScenario> {

    private val ma
        get() = intent.getSerializableExtra(Extras.EXTRA_MARKETING_ACTIVITY) as? MarketingActivity

    private lateinit var passport:Passport

    private val adapter = SimpleDataBindAdapter<ItemMarketingScenarioBinding,MarketingActivityScenario>(
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
        setBanner()
    }

    private fun setBanner() {
        val ivBanner = findViewById<ImageView>(R.id.iv_banner)
        ma?.bannerImgUrl?.let {
            GlideApp.with(ivBanner)
                    .load(it)
                    .into(ivBanner)
        }
        ma?.bannerLinkUrl?.let { url ->
            ivBanner.setOnClickListener {
                navigateTo(WebPageActivity::class.java) {
                    data = Uri.parse(url)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        tryLoad()
    }

    private fun tryLoad() {
        if (checkPreconditions()) {
            loadScenarios()
            title = ma!!.name
        }
    }

    private fun checkPreconditions(): Boolean {
        val currentPassport = App.get().passportRepository.getCurrentPassport()
        return if (currentPassport!=null && ma !=null) {
            passport = currentPassport
            true
        }else{
            postOnMainThread {
                finish()
            }
            false
        }
    }

    override fun onItemClick(item: MarketingActivityScenario?, position: Int, viewId: Int) {
        item?.let {
            when(it.qualification){
                MarketingActivityScenario.Qualification.REDEEMED -> {
                    //no op
                }
                MarketingActivityScenario.Qualification.AVAILABLE -> {
                    redeemScenario(it.code,passport.address)
                }
                else -> {
                    // cert undone
                    navigateTo(MyCreditActivity::class.java)
                }
            }
        }
    }

    private fun redeemScenario(code: String, address: String) {
        App.get().marketingApi.applyRedeemToken(code,address)
                .compose(Result.checkedAllowingNull(""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _->
                    loadScenarios()
                }
    }

    private fun loadScenarios() {
        App.get().marketingApi.queryScenario(ma!!.code,passport.address)
                .compose(Result.checked())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list->
                    adapter.setList(list)
                }
    }
}