package io.wexchain.android.dcc

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.RequestCodes
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.tools.isAddressShortNameValid
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityAddBeneficiaryAddressBinding
import io.wexchain.digitalwallet.util.isEthAddress

class AddBeneficiaryAddressActivity : BindActivity<ActivityAddBeneficiaryAddressBinding>() {
    override val contentLayoutId: Int = R.layout.activity_add_beneficiary_address

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initClicks()
    }

    private fun initClicks() {
        binding.ibScanAddress.setOnClickListener {
            startActivityForResult(Intent(this, QrScannerActivity::class.java).apply {
                putExtra(Extras.EXPECTED_SCAN_TYPE, QrScannerActivity.SCAN_TYPE_ADDRESS)
            }, RequestCodes.SCAN)
        }
        binding.btnAdd.setOnClickListener {
            val inputAddr = binding.etInputAddress.text.toString()
            val inputShortName = binding.etInputAddressShortName.text.toString()
            if(isEthAddress(inputAddr) && isAddressShortNameValid(inputShortName)){
                val setDefault = binding.checkDefaultAddress.isChecked
                App.get().passportRepository.addBeneficiaryAddress(BeneficiaryAddress(inputAddr,inputShortName))
                if (setDefault) {
                    App.get().passportRepository.setDefaultBeneficiaryAddress(inputAddr)
                }
                toast("添加成功")
                finish()
            }else{
                toast("请输入有效的地址和简称")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.SCAN -> {
                val address = data?.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                if (address != null ) {
                    findViewById<EditText>(R.id.et_input_address).setText(address)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
