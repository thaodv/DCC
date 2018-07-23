package io.wexchain.android.dcc.modules.trans.activity

import android.os.Bundle
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.dcc.R


class AcrossTransRecordActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_across_trans_record)
        initToolbar()
    }
}
