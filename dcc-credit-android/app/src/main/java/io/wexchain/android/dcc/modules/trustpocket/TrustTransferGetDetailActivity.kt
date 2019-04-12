package io.wexchain.android.dcc.modules.trustpocket;


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
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
import java.math.BigDecimal

class TrustTransferGetDetailActivity : BindActivity<ActivityTrustTransferGetDetailBinding>() {

    private val mId get() = intent.getStringExtra("id")
    private val mType get() = intent.getStringExtra("type")

    lateinit var tag: String

    override val contentLayoutId: Int get() = R.layout.activity_trust_transfer_get_detail


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        title = ""

        getTransferOrder(mId)

        if ("1" == mType) {
            tag = "+ "
        } else {
            tag = "- "
            binding.rvList.visibility = View.GONE
            binding.rlToAccount.visibility = View.GONE
            binding.vLine.visibility = View.GONE
        }
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
                    binding.tvAmount.text = tag + it.amount.decimalValue + it.assetCode
                    binding.tvStatus.text = ViewModelHelper.showTrustTransferStatus(it.status)
                    binding.tvOrderId.text = mId
                    binding.tvTime.text = ViewModelHelper.showRedPacketInviteTime(it.createdTime)

                    if ("1" == mType) {

                        val adapter = FeeAdapter(it.assetCode)
                        adapter.setList(it.splitAmountDetails)
                        binding.rvList.adapter = adapter

                        var fee: BigDecimal = BigDecimal.ZERO

                        if (null == it.splitAmountDetails) {

                        } else {
                            if (it.splitAmountDetails!!.isEmpty()) {

                            } else {
                                for (ss in it.splitAmountDetails!!) {
                                    fee += ss.amount.decimalValue.toBigDecimal()
                                }
                            }
                        }
                        binding.tvAccount.text = it.amount.decimalValue.toBigDecimal().subtract(fee).toPlainString() + " " + it.assetCode
                    }

                    binding.tvTitle.text = it.memo

                }, {
                    toast(it.message.toString())
                })
    }

    class FeeAdapter(assetCode: String) : DataBindAdapter<ItemTrustTransferDetailGetBinding, GetTransferOrderBean.SplitAmountDetailsBean>(
            layout = R.layout.item_trust_transfer_detail_get,
            itemDiffCallback = itemDiffCallback({ a, b -> a.memo == b.memo })
    ) {
        val assetCode: String = assetCode

        override fun bindData(binding: ItemTrustTransferDetailGetBinding, item: GetTransferOrderBean.SplitAmountDetailsBean?) {
            binding.bean = item
            binding.assetCode = assetCode
        }
    }


}
