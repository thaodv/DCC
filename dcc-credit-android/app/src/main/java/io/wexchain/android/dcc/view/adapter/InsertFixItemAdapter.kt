package io.wexchain.android.dcc.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.wexchain.dcc.R

/**
 * Created by lulingzhi on 2017/12/11.
 */
class InsertFixItemAdapter<OVH : RecyclerView.ViewHolder>(
        val originalAdapter: RecyclerView.Adapter<OVH>,
        val insertFixViewProvider: InsertFixViewProvider
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_INSERT_FIXED = R.id.constant_fix_insert_item_type_id
    }

    init {
        if (originalAdapter.hasStableIds()) {
            setHasStableIds(true)
        }
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        originalAdapter.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        originalAdapter.unregisterAdapterDataObserver(observer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isFixedInsertType(viewType)) {
            Holder(itemView = insertFixViewProvider.inflateBottomView(parent))
        } else {
            originalAdapter.onCreateViewHolder(parent, viewType)
        }
    }

    private fun isFixedInsertType(viewType: Int): Boolean {
        return viewType == TYPE_INSERT_FIXED
    }

    private fun isInsertedPosition(position: Int): Boolean {
        val insertionPos = insertFixViewProvider.insertionPos
        val oCount = originalAdapter.itemCount
        return (oCount < insertionPos && position == oCount) || position == insertionPos
    }

    override fun getItemCount(): Int {
        return originalAdapter.itemCount + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isInsertedPosition(position)) {
            insertFixViewProvider.onBind(holder.itemView, position)
        } else {
            @Suppress("UNCHECKED_CAST")
            originalAdapter.onBindViewHolder(holder as OVH, getOriginalPos(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isInsertedPosition(position)) {
            TYPE_INSERT_FIXED
        } else {
            originalAdapter.getItemViewType(getOriginalPos(position))
        }
    }

    fun getOriginalPos(position: Int) =
            if (position > insertFixViewProvider.insertionPos) position - 1 else position

    override fun getItemId(position: Int): Long {
        return if (isInsertedPosition(position)) {
            RecyclerView.NO_ID//todo consider another id
        } else {
            originalAdapter.getItemId(getOriginalPos(position))
        }
    }

    interface InsertFixViewProvider {
        fun inflateBottomView(parent: ViewGroup): View
        fun onBind(bottomView: View?, position: Int)
        val insertionPos : Int
    }

    private class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}