package io.wexchain.android.dcc.modules.paymentcode

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.dcc.R

class PaymentUnOpenActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_un_open)
        initToolbar()

        findViewById<TextView>(R.id.tv_use).onClick {
            val sp = getSharedPreferences("setting", Context.MODE_PRIVATE)
            sp.edit().putBoolean("payment_first_into", false).commit()
            navigateTo(RepaymentQuickReceiptActivity::class.java)
            finish()
        }

    }
}
