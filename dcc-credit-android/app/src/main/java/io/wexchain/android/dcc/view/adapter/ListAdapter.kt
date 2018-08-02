package io.wexchain.android.dcc.view.adapter

import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.util.DiffUtil

/**
 *Created by liuyang on 2018/7/31.
 */
class ListAdapter<B : ViewDataBinding, T>(itemViewClickListener: ItemViewClickListener<T>,
                                          @LayoutRes private val layouts: Int,
                                          itemDiffCallback: DiffUtil.ItemCallback<T>,
                                          var binDatas: (B, T?) -> Unit) : DataBindAdapter<B, T>(
        layout = layouts,
        itemDiffCallback = itemDiffCallback,
        itemViewClickListener = itemViewClickListener
) {
    override fun bindData(binding: B, item: T?) {
        binDatas.invoke(binding, item)
    }


}