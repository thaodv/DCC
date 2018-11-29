package io.wexchain.android.dcc.modules.cashloan.act

import android.os.Bundle
import android.widget.Button
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.tools.PickerHelper
import io.wexchain.dcc.R

/**
 *Created by liuyang on 2018/11/28.
 */
class CashLoanActivity : BaseCompatActivity() {

    private val picker by lazy {
        PickerHelper()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashloan)
        initToolbar()
        picker.init(this)
        findViewById<Button>(R.id.btn).setOnClickListener {
            if (picker.isLoad) {
                picker.showPickerView { ti, t2, t3 ->
                    toast("$ti - $t2 - $t3")
                }
            } else {
                toast("加载未完成")
            }
        }
    }

}