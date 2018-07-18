package io.wexchain.android.dcc.view.adapters

import android.support.v4.util.ObjectsCompat
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemTransactionRecordBinding
import io.wexchain.digitalwallet.EthsTransaction

/**
 * Created by sisel on 2018/1/24.
 */
class TransactionRecordAdapter(
        val ownerAddress: String,
        itemViewClickListener: ItemViewClickListener<EthsTransaction>? = null
) : DataBindAdapter<ItemTransactionRecordBinding, EthsTransaction>(
        R.layout.item_transaction_record,
        itemDiffCallback({ a, b ->
            a.digitalCurrency.chain == b.digitalCurrency.chain && ObjectsCompat.equals(a.txId, b.txId)
        })
        , itemViewClickListener
) {
    override fun bindData(binding: ItemTransactionRecordBinding, item: EthsTransaction?) {
        binding.tx = item
        binding.owner = ownerAddress
    }
}
