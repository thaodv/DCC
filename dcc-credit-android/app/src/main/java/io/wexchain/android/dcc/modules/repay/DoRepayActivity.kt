package io.wexchain.android.dcc.modules.repay

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.navigateTo
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.chain.ScfOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.domain.LoanRepaymentBill

class DoRepayActivity : BaseCompatActivity() {

    private val bill
        get() = intent.getSerializableExtra(Extras.EXTRA_LOAN_REPAY_BILL) as? LoanRepaymentBill

    private lateinit var mTvTip: TextView
    private lateinit var mBtSubmit: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
        setContentView(R.layout.activity_do_repay)
        mTvTip = findViewById(R.id.tv_tip)
        mBtSubmit = findViewById(R.id.btn_submit)

        if (null != bill) {
            mTvTip.text = "还币金额" + bill!!.amount.subtract(bill!!.noPayAmount) + bill!!.assetCode + "已收到，请点击确认还币完成还币"
        }


        mBtSubmit.setOnClickListener {
            ScfOperations.withScfTokenInCurrentPassport("") {
                App.get().scfApi.confirmRepayment(it, bill!!.chainOrderId)
            }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        showLoadingDialog()
                    }
                    .doFinally {
                        hideLoadingDialog()
                    }
                    .subscribe { _ ->
                        finish()
                        navigateTo(LoanRepayResultActivity::class.java)
                    }
        }

    }

}
