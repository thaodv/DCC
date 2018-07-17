package io.wexchain.android.dcc.modules.addressbook.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.repo.db.TransRecord
import io.wexchain.android.dcc.tools.CommonUtils
import io.wexchain.android.dcc.view.dialog.DeleteAddressBookDialog
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
            binding.tvWalletAddress.text = beneficiaryAddress!!.address
            binding.tvAddressName.text = beneficiaryAddress!!.shortName
            binding.ivAvatar.setImageURI(CommonUtils.str2Uri(beneficiaryAddress!!.avatarUrl))

            initClick()
        }
    }

    private fun initClick() {
        binding.btEdit.setOnClickListener {
            navigateTo(EditBeneficiaryAddressActivity::class.java) {
                putExtra(Extras.EXTRA_BENEFICIARY_ADDRESS, beneficiaryAddress as Serializable)
            }
            finish()
        }

        binding.btDelete.setOnClickListener {

            val deleteDialog = DeleteAddressBookDialog(this)
            deleteDialog.mTvText.text = "确定要删除该地址吗？"
            deleteDialog.mTvText.gravity = Gravity.CENTER_HORIZONTAL
            deleteDialog.setOnClickListener(object : DeleteAddressBookDialog.OnClickListener {
                override fun cancel() {}

                override fun sure() {

                    App.get().passportRepository.getTransRecordByAddress(beneficiaryAddress!!.address).observe(this@AddressDetailActivity, Observer {
                        var mTrans: ArrayList<TransRecord> = ArrayList()
                        if (null != it) {
                            for (item in it) {
                                mTrans.add(TransRecord(item.id, item.address, item.shortName, avatarUrl = item.avatarUrl, is_add = 0, create_time = item.create_time, update_time = SystemClock.currentThreadTimeMillis()))
                            }
                            App.get().passportRepository.updateTransRecord(mTrans)
                        }
                    })
                    App.get().passportRepository.removeBeneficiaryAddress(beneficiaryAddress!!)

                    finish()
                }
            })
            deleteDialog.show()
        }
    }
}
