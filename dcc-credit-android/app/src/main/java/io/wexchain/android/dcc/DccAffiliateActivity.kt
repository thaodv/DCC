package io.wexchain.android.dcc

import android.content.ClipData
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityDccAffiliateBinding

class DccAffiliateActivity : BindActivity<ActivityDccAffiliateBinding>() {
    override val contentLayoutId: Int
        get() = R.layout.activity_dcc_affiliate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initClicks()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        val passed = CertOperations.isIdCertPassed()
        binding.certDone = passed
        if (passed) {
            ScfOperations
                .getScfAccountInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { acc->
                    binding.account = acc
                }
        }
    }

    private fun initClicks() {
        binding.tvAffRecords.setOnClickListener {
            navigateTo(DccAffiliateRecordsActivity::class.java)
        }
        binding.btnCopyInviteCode.setOnClickListener {
            binding.account?.inviteCode?.let {
                getClipboardManager().primaryClip = ClipData.newPlainText("邀请码",it)
                toast("已复制到剪贴板")
            }
        }
        binding.btnToCert.setOnClickListener {
            PassportOperations.ensureCaValidity(this) {
                navigateTo(SubmitIdActivity::class.java)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dcc_aff, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_aff_terms -> {
                navigateTo(DccAffiliateTermsActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
