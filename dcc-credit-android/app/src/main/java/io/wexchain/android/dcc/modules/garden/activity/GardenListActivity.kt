package io.wexchain.android.dcc.modules.garden.activity

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityGardenListBinding
import io.wexchain.dcc.databinding.ItemGardenListBinding
import io.wexchain.dccchainservice.domain.ChangeOrder

/**
 *Created by liuyang on 2018/11/7.
 */
class GardenListActivity : BindActivity<ActivityGardenListBinding>(), ItemViewClickListener<ChangeOrder> {

    override fun onItemClick(item: ChangeOrder?, position: Int, viewId: Int) {

    }

    override val contentLayoutId: Int get() = R.layout.activity_garden_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initView()
    }

    private fun initView() {
        binding.rvList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.rvList.adapter = adapter
        binding.vm = getViewModel()
        binding.srlList.setOnRefreshListener { srl ->
            binding.vm!!.refresh { srl.finishRefresh() }
        }
        binding.srlList.setOnLoadMoreListener { srl ->
            binding.vm!!.loadNext { srl.finishLoadMore() }
        }
        binding.srlList.setRefreshHeader(ClassicsHeader(this))
        binding.srlList.setRefreshFooter(ClassicsFooter(this))
        binding.vm!!.refresh()
    }

    private val adapter = SimpleDataBindAdapter<ItemGardenListBinding, ChangeOrder>(
            layoutId = R.layout.item_garden_list,
            variableId = BR.order,
            itemViewClickListener = this
    )

}
