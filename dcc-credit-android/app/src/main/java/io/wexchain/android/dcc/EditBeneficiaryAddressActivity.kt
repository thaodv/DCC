package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.RequestCodes
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.tools.isAddressShortNameValid
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityEditBeneficiaryAddressBinding
import io.wexchain.digitalwallet.util.isEthAddress

class EditBeneficiaryAddressActivity : BindActivity<ActivityEditBeneficiaryAddressBinding>() {

    override val contentLayoutId: Int
        get() = R.layout.activity_edit_beneficiary_address

    private val ba
        get() = intent.getSerializableExtra(Extras.EXTRA_BENEFICIARY_ADDRESS) as? BeneficiaryAddress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        val beneficiaryAddress = ba
        if (beneficiaryAddress == null) {
            postOnMainThread { finish() }
        } else {
            App.get().passportRepository.defaultBeneficiaryAddress.observe(this, Observer {})
            binding.etInputAddress.setText(beneficiaryAddress.address)
            binding.etInputAddressShortName.setText(beneficiaryAddress.shortName)
            binding.checkDefaultAddress.isChecked = App.get().passportRepository.defaultBeneficiaryAddress.value == beneficiaryAddress.address
            initClicks()
        }
    }

    private fun initClicks() {
        binding.ibScanAddress.setOnClickListener {
            startActivityForResult(Intent(this, QrScannerActivity::class.java).apply {
                putExtra(Extras.EXPECTED_SCAN_TYPE, QrScannerActivity.SCAN_TYPE_ADDRESS)
            }, RequestCodes.SCAN)
        }
        binding.btnSave.setOnClickListener {
            val inputAddr = binding.etInputAddress.text.toString()
            val inputShortName = binding.etInputAddressShortName.text.toString()
            if (!isEthAddress(inputAddr)) {
                toast("地址格式不符,请核对后重新输入")
            } else if (!isAddressShortNameValid(inputShortName)) {
                toast("请输入有效的简称")
            } else {
                val setDefault = binding.checkDefaultAddress.isChecked
                val passportRepository = App.get().passportRepository
                passportRepository.addBeneficiaryAddress(BeneficiaryAddress(inputAddr, inputShortName))
                if (setDefault) {
                    passportRepository.setDefaultBeneficiaryAddress(inputAddr)
                } else if (passportRepository.defaultBeneficiaryAddress.value == inputAddr) {
                    passportRepository.setDefaultBeneficiaryAddress(null)
                }
                toast("修改成功")
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.SCAN -> {
                val address = data?.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                if (address != null) {
                    findViewById<EditText>(R.id.et_input_address).setText(address)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


}
