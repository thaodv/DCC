package io.wexchain.android.dcc.view.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.wexchain.android.dcc.view.adapter.multitype.TypeViewBinder

open class MultiTypeListAdapter<T>(
    itemDiffCallback: DiffUtil.ItemCallback<T> = defaultItemDiffCallback(),
    val itemTypeDispatcher: (T) -> Int,
    vararg binders: Pair<Int, TypeViewBinder<T, *>>
) : ListAdapter<T, RecyclerView.ViewHolder>(itemDiffCallback) {
    private val typeBindMap = SparseArrayCompat<TypeViewBinder<T, *>>()

    init {
        binders.forEach { (type, binder) ->
            typeBindMap.put(type, binder)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return itemTypeDispatcher(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val typeViewBinder = typeBindMap[viewType]
        typeViewBinder?:throw IllegalStateException("unable to find Type-View binder")
        return typeViewBinder.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        typeBindMap[holder.itemViewType]?.bindHolder(holder, item)
    }

}