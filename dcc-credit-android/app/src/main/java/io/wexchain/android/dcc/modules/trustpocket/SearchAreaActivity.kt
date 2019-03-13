package io.wexchain.android.dcc.modules.trustpocket

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.google.gson.Gson
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.constant.AreaCode
import io.wexchain.android.common.constant.AreaCodeBean
import io.wexchain.android.common.resultOk
import io.wexchain.android.dcc.view.adapter.DataBindAdapter
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.itemDiffCallback
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemSmsAreaBinding


class SearchAreaActivity : BaseCompatActivity(), ItemViewClickListener<AreaCodeBean.AreaCodeItemBean> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_area)
        initToolbar()

        val mRvList: RecyclerView = findViewById(R.id.rv_list)

        val mDatas: AreaCodeBean = Gson().fromJson(AreaCode.res, AreaCodeBean::class.java)

        val adapter = AreaAdapter(this)
        adapter.setList(mDatas.res)
        mRvList.adapter = adapter
    }

    override fun onItemClick(item: AreaCodeBean.AreaCodeItemBean?, position: Int, viewId: Int) {
        resultOk {
            putExtra("dialCode", item!!.dial_code)
        }
    }

    class AreaAdapter(
            itemViewClickListener: ItemViewClickListener<AreaCodeBean.AreaCodeItemBean>
    ) : DataBindAdapter<ItemSmsAreaBinding, AreaCodeBean.AreaCodeItemBean>(
            layout = R.layout.item_sms_area,
            itemDiffCallback = itemDiffCallback({ a, b -> a.country_code == b.country_code }),
            itemViewClickListener = itemViewClickListener
    ) {

        override fun bindData(binding: ItemSmsAreaBinding, item: AreaCodeBean.AreaCodeItemBean?) {
            binding.area = item
        }
    }

}
