package io.wexchain.android.dcc

import android.os.Bundle
import android.view.View
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.setWindowExtended
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.CertOperations
import io.wexchain.android.dcc.modules.repay.LoanRecordsActivity
import io.wexchain.android.dcc.tools.checkonMain
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.ChainGateway

class LoanActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan)
        initToolbar(false)
        setWindowExtended()
        initClicks()
    }

    private fun initClicks() {
        findViewById<View>(R.id.card_my_address).setOnClickListener {
            navigateTo(BeneficiaryAddressesManagementActivity::class.java)
        }
        findViewById<View>(R.id.card_start_loan).setOnClickListener {

            App.get().chainGateway.getCertData(App.get().passportRepository.currPassport.value!!.address, ChainGateway.BUSINESS_COMMUNICATION_LOG)
                    .checkonMain()
                    .subscribe({
                        it.content.let {
                            it.let {
                                if (0L != it!!.expired) {
                                    CertOperations.saveCmLogCertExpired(it!!.expired)
                                }
                            }
                        }
                    }, {})
            navigateTo(LoanProductListActivity::class.java)
        }
        findViewById<View>(R.id.card_my_loan).setOnClickListener {
            navigateTo(LoanRecordsActivity::class.java)
        }
        findViewById<View>(R.id.card_incentive_program).setOnClickListener {
            navigateTo(IncentiveProgramActivity::class.java)
        }
    }
}
