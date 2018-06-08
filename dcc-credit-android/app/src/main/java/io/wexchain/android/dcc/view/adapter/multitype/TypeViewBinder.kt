package io.wexchain.android.dcc.view.adapter.multitype

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

interface TypeViewBinder<T,VH:RecyclerView.ViewHolder>{
    fun bindHolder(holder: RecyclerView.ViewHolder, item: T){
        @Suppress("UNCHECKED_CAST")
        bind(holder as VH,item)
    }

    fun bind(viewHolder: VH, item: T)

    fun createViewHolder(parent: ViewGroup): VH
}