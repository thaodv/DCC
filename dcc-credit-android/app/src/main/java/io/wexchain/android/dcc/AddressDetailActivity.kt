package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.os.Bundle
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.tools.CommonUtils
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityAddressDetailBinding
import java.io.Serializable

class AddressDetailActivity : BindActivity<ActivityAddressDetailBinding>() {

    override val contentLayoutId: Int = R.layout.activity_address_detail

    private val ba
        get() = intent.getSerializableExtra(Extras.EXTRA_BENEFICIARY_ADDRESS) as? BeneficiaryAddress

    private val beneficiaryAddress: BeneficiaryAddress?
        get() {
            return ba
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        title = "地址详情"

        if (beneficiaryAddress == null) {
            postOnMainThread { finish() }
        } else {
            App.get().passportRepository.defaultBeneficiaryAddress.observe(this, Observer {})
            binding.tvWalletAddress.setText(beneficiaryAddress!!.address)
            binding.tvAddressName.setText(beneficiaryAddress!!.shortName)
            binding.ivAvatar.setImageURI(CommonUtils.str2Uri(beneficiaryAddress!!.avatarUrl))

            initClick()
        }
    }

    fun initClick() {
        binding.btEdit.setOnClickListener {
            navigateTo(EditBeneficiaryAddressActivity::class.java) {
                putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS, beneficiaryAddress as Serializable)
            }
            finish()
        }

        binding.btDelete.setOnClickListener {
            CustomDialog(this)
                    .apply {
                        setTitle("提示")
                        textContent = "确认删除此地址吗？"
                    }
                    .withPositiveButton("删除") {
                        App.get().passportRepository.removeBeneficiaryAddress(beneficiaryAddress!!)
                        finish()
                        true

                    }
                    .withNegativeButton()
                    .assembleAndShow()
        }

    }


}
