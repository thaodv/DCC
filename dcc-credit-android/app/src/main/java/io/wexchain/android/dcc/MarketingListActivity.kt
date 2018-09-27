package io.wexchain.android.dcc

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.view.adapter.*
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemMarketingActivityBinding
import io.wexchain.dccchainservice.domain.MarketingActivity
import io.wexchain.dccchainservice.domain.Result

class MarketingListActivity : BaseCompatActivity(), ItemViewClickListener<MarketingActivity> {

    private val adapter = Adapter(this::onItemClick)

    private val wrappedAdapter = BottomMoreItemsAdapter(
        adapter,
        object : BottomMoreItemsAdapter.BottomViewProvider {
            override fun inflateBottomView(parent: ViewGroup): View {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_more_candy, parent, false)
                view.findViewById<View>(R.id.item_business_contact).setOnClickListener {
                    toBusinessEmail()
                }
                return view
            }

            override fun onBind(bottomView: View?, position: Int) {
            }
        }
    )

    private fun toBusinessEmail() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.bitexpress_info_mail_address)))
        startActivity(Intent.createChooser(intent, "发送邮件"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketing_list)
        initToolbar()
        val rvList = findViewById<RecyclerView>(R.id.rv_list)
        rvList.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL).apply {
            setDrawable(ContextCompat.getDrawable(this@MarketingListActivity,R.drawable.divider_space)!!)
        })
        rvList.adapter = wrappedAdapter
        loadMa()
    }

    fun onItemClick(position: Int, viewId: Int) {
        val pos = position
        onItemClick(adapter.getItemOnPos(pos), pos, viewId)
    }

    override fun onItemClick(item: MarketingActivity?, position: Int, viewId: Int) {
        item?.let {
            when(it.code){
                "10002"->{
                    navigateTo(DccAffiliateActivity::class.java)
                }
                "10003"->{
                    navigateTo(DccEcoRewardsActivity::class.java)
                }
                "10004"->{
                    navigateTo(MineRewardsActivity::class.java)
                }
                else->{
                    if (it.status == MarketingActivity.Status.STARTED) {
                        navigateTo(MarketingScenariosActivity::class.java) {
                            putExtra(Extras.EXTRA_MARKETING_ACTIVITY, it)
                        }
                    }
                }
            }
        }
    }

    private fun loadMa() {
        App.get().marketingApi.queryActivity()
            .compose(Result.checked())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                showLoadingDialog()
            }
            .doFinally {
                hideLoadingDialog()
            }
            .subscribe({ list ->
                adapter.setList(list)
            }, {
                toast(R.string.common_service_error_toast)
            })
    }

    private class Adapter(val onPosClick: (Int, Int) -> Unit) :
        DataBindAdapter<ItemMarketingActivityBinding, MarketingActivity>(
            R.layout.item_marketing_activity,
            itemDiffCallback = defaultItemDiffCallback()
        ) {
        override fun bindData(binding: ItemMarketingActivityBinding, item: MarketingActivity?) {
            binding.ma = item
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BindingViewHolder<ItemMarketingActivityBinding> {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemMarketingActivityBinding = DataBindingUtil.inflate(layoutInflater, layout, parent, false)
            return ClickAwareHolder(binding, onPosClick)
        }
    }
}
