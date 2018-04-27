package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.BeneficiaryAddressAdapter
import io.wexchain.dcc.R

class BeneficiaryAddressesManagementActivity : BaseCompatActivity(), ItemViewClickListener<BeneficiaryAddress> {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beneficiary_addresses_management)
        initToolbar()
        initData()
    }

    private fun initData() {
        findViewById<View>(R.id.btn_add_address).setOnClickListener {
            navigateTo(AddBeneficiaryAddressActivity::class.java)
        }
        val adapter = BeneficiaryAddressAdapter(getViewModel(),this)
        adapter.lifecycleOwner = this
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
        val passportRepository = App.get().passportRepository
        passportRepository.beneficiaryAddresses.observe(this, Observer {
            adapter.setList(it)
        })
    }

    override fun onItemClick(item: BeneficiaryAddress?, position: Int, viewId: Int) {
        item?.let {
            when(viewId){
                R.id.btn_delete->{

                }
                R.id.btn_edit->{

                }
            }
        }
    }
}
