package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.view.adapter.ItemViewClickListener
import io.wexchain.android.dcc.view.adapters.BeneficiaryAddressAdapter
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.vm.DefaultBeneficiaryAddressVm
import io.wexchain.dcc.R
import java.io.Serializable

class BeneficiaryAddressesManagementActivity : BaseCompatActivity(), ItemViewClickListener<BeneficiaryAddress> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beneficiary_addresses_management)
        initToolbar()
        initData()
    }

    override fun onResume() {
        super.onResume()
        println(App.get().passportRepository.defaultBeneficiaryAddress.value)
    }

    private fun initData() {
        findViewById<View>(R.id.btn_add_address).setOnClickListener {
            navigateTo(AddBeneficiaryAddressActivity::class.java)
        }
        val defaultBeneficiaryAddressVm = getViewModel<DefaultBeneficiaryAddressVm>()
        defaultBeneficiaryAddressVm.defaultAddr.observe(this, Observer {  })
        defaultBeneficiaryAddressVm.walletAddr.observe(this, Observer {  })
        val adapter = BeneficiaryAddressAdapter(defaultBeneficiaryAddressVm,this)
        adapter.lifecycleOwner = this
        findViewById<RecyclerView>(R.id.rv_list).adapter = adapter
        val passportRepository = App.get().passportRepository
        passportRepository.beneficiaryAddresses.observe(this, Observer {
            adapter.setList(it)
        })
    }

    override fun onItemClick(item: BeneficiaryAddress?, position: Int, viewId: Int) {
        item?.let {ba->
            when(viewId){
                R.id.btn_delete->{
                    confirmDelete(ba)
                }
                R.id.btn_edit->{
                    toEdit(ba)
                }
            }
        }
    }

    private fun toEdit(ba: BeneficiaryAddress) {
        navigateTo(EditBeneficiaryAddressActivity::class.java) {
            putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS, ba as Serializable)
        }
    }

    private fun confirmDelete(ba: BeneficiaryAddress) {
        CustomDialog(this)
            .apply {
                setTitle("确认删除以下信息")
                textContent = "${ba.shortName}\n${ba.address}"
            }
            .withPositiveButton("删除") {
                App.get().passportRepository.removeBeneficiaryAddress(ba)
                true
            }
            .withNegativeButton()
            .assembleAndShow()
    }
}
