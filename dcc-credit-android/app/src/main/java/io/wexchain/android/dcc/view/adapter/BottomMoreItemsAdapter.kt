package io.wexchain.android.dcc.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.wexchain.auth.R

/**
 * Created by lulingzhi on 2017/12/11.
 */
class BottomMoreItemsAdapter<OVH : RecyclerView.ViewHolder>(
        val originalAdapter: RecyclerView.Adapter<OVH>,
        val bottomViewProvider: BottomViewProvider
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_BOTTOM = R.id.constant_bottom_more_item_type_id
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
        return if (isBottomType(viewType)) {
            Holder(itemView = bottomViewProvider.inflateBottomView(parent))
        } else {
            originalAdapter.onCreateViewHolder(parent, viewType)
        }
    }

    private fun isBottomType(viewType: Int): Boolean {
        return viewType == TYPE_BOTTOM
    }

    private fun isBottomPosition(position: Int): Boolean {
        return position == originalAdapter.itemCount
    }

    override fun getItemCount(): Int {
        return originalAdapter.itemCount + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isBottomPosition(position)) {
            bottomViewProvider.onBind(holder.itemView, position)
        } else {
            @Suppress("UNCHECKED_CAST")
            originalAdapter.onBindViewHolder(holder as OVH, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isBottomPosition(position)) {
            TYPE_BOTTOM
        } else {
            originalAdapter.getItemViewType(position)
        }
    }

    override fun getItemId(position: Int): Long {
        return if (isBottomPosition(position)) {
            RecyclerView.NO_ID//todo consider another id
        } else {
            originalAdapter.getItemId(position)
        }
    }

    interface BottomViewProvider {
        fun inflateBottomView(parent: ViewGroup?): View
        fun onBind(bottomView: View?, position: Int)
    }

    private class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}