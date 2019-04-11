package io.wexchain.android.dcc.modules.trustpocket;


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.android.dcc.vm.ViewModelHelper
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityTrustTransferGetDetailBinding
import io.wexchain.dcc.databinding.ItemTrustTransferDetailGetBinding
import io.wexchain.dccchainservice.domain.trustpocket.GetTransferOrderBean
import io.wexchain.ipfs.utils.doMain

class TrustTransferGetDetailActivity : BindActivity<ActivityTrustTransferGetDetailBinding>() {

    private val mId get() = intent.getStringExtra("id")

    override val contentLayoutId: Int get() = R.layout.activity_trust_transfer_get_detail

    lateinit var mRvList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        title = ""

        mRvList = findViewById(R.id.rv_list)

        getTransferOrder(mId)
    }

    @SuppressLint("SetTextI18n")
    private fun getTransferOrder(id: String) {
        GardenOperations
                .refreshToken {
                    App.get().marketingApi.getTransferOrder(it, id).check()
                }
                .doMain()
                .withLoading()
                .subscribe({
                    binding.tvAmount.text = it.amount.decimalValue + it.assetCode
                    binding.tvStatus.text = ViewModelHelper.showTrustTransferStatus(it.status)
                    binding.tvOrderId.text = mId
                    binding.tvTime.text = ViewModelHelper.showRedPacketInviteTime(it.createdTime)

                    val adapter = FeeAdapter()
                    adapter.setList(it.splitAmountDetails)
                    mRvList.adapter = adapter

                    binding.tvAccount.text = it.amount.decimalValue + it.assetCode
                    binding.tvTitle.text = it.memo

                }, {
                    toast(it.message.toString())
                })
    }

    class FeeAdapter : DataBindAdapter<ItemTrustTransferDetailGetBinding, GetTransferOrderBean.SplitAmountDetailsBean>(
            layout = R.layout.item_trust_transfer_detail_get,
            itemDiffCallback = itemDiffCallback({ a, b -> a.memo == b.memo })
    ) {

        override fun bindData(binding: ItemTrustTransferDetailGetBinding, item: GetTransferOrderBean.SplitAmountDetailsBean?) {
            binding.bean = item
        }
    }


}
