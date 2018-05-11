package io.wexchain.android.dcc.view.adapter

import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import io.wexchain.android.common.kotlin.weak
import java.lang.ref.WeakReference

/**
 * Created by lulingzhi on 2017/8/22.
 * for single type
 */
abstract class DataBindAdapter<B : ViewDataBinding, T>(
        @LayoutRes val layout: Int,
        itemDiffCallback: DiffUtil.ItemCallback<T>,
        private val itemViewClickListener: ItemViewClickListener<T>? = null,
        private vararg val clickAwareViewIds: Int = intArrayOf()
) : ListAdapter<T, BindingViewHolder<B>>(itemDiffCallback) {

    var lifecycleOwner:LifecycleOwner? by weak()

    fun setList(newList: List<T>?) {
        submitList(newList)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<B>, position: Int) {
        bindData(holder.binding, getItem(position))
    }

    abstract fun bindData(binding: B, item: T?)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<B> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: B = DataBindingUtil.inflate(layoutInflater, layout, parent, false)
        lifecycleOwner?.let {
            binding.setLifecycleOwner(it)
        }
        return if (itemViewClickListener != null) {
            ClickAwareHolder(binding, this::onPositionClick, *clickAwareViewIds)
        } else {
            BindingViewHolder(binding)
        }
    }

    private fun onPositionClick(position: Int, @IdRes viewId: Int) {
        itemViewClickListener?.onItemClick(getItem(position), position, viewId)
    }

    public fun getItemOnPos(pos:Int): T {
        return getItem(pos)
    }
}