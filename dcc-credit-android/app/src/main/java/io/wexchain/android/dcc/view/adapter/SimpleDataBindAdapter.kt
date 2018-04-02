package io.wexchain.android.dcc.view.adapter

import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes

/**
 * Created by lulingzhi on 2017/10/9.
 */
open class SimpleDataBindAdapter<B : ViewDataBinding, T>(
        @LayoutRes layoutId: Int,
        private val variableId: Int,
        itemViewClickListener: ItemViewClickListener<T>? = null,
        vararg clickAwareViewIds: Int = intArrayOf()
) : DataBindAdapter<B, T>(
    layout = layoutId,
    itemDiffCallback = defaultItemDiffCallback<T>(),
    itemViewClickListener = itemViewClickListener,
    clickAwareViewIds = *clickAwareViewIds
) {
    override fun bindData(binding: B, item: T?) {
        binding.setVariable(variableId, item)
    }
}