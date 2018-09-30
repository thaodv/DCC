package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.widget.Button
import io.wexchain.android.common.onClick
import io.wexchain.android.common.resultOk
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapter.SimpleDataBindAdapter
import io.wexchain.dcc.BR
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ItemChooseBeneficiaryAddressBinding
import org.jetbrains.anko.find

class ChooseBeneficiaryAddressActivity : BaseCompatActivity(), ItemViewClickListener<BeneficiaryAddress> {

    private val adapter = SimpleDataBindAdapter<ItemChooseBeneficiaryAddressBinding, BeneficiaryAddress>(
            layoutId = R.layout.item_choose_beneficiary_address,
            variableId = BR.ba,
            itemViewClickListener = this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_beneficiary_address)
        initToolbar()
        App.get().passportRepository.beneficiaryAddresses.observe(this, Observer {
            adapter.setList(it)
        })
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
        find<Button>(R.id.bt_new_address).onClick {
            startActivity(Intent(this, AddBeneficiaryAddressActivity::class.java))
        }
    }

    override fun onItemClick(item: BeneficiaryAddress?, position: Int, viewId: Int) {
        item?.let {
            resultOk {
                putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS, it)
            }
        }
    }
}
