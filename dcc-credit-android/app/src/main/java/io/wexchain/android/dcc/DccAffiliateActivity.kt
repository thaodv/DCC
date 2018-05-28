package io.wexchain.android.dcc

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.base.BindActivity
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

    private fun initClicks() {
        binding.tvAffRecords.setOnClickListener {
            navigateTo(DccAffiliateRecordsActivity::class.java)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dcc_aff,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId){
            R.id.action_aff_terms -> {
                navigateTo(DccAffiliateTermsActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
