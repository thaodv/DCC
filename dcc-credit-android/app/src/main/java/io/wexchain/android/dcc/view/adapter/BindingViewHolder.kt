package io.wexchain.android.dcc.view.adapter

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

/**
 * Created by lulingzhi on 2017/8/22.
 */
open class BindingViewHolder<out B:ViewDataBinding>(val binding:B):RecyclerView.ViewHolder(binding.root)