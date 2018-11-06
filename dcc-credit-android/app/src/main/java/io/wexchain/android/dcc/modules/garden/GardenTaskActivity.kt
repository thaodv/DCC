package io.wexchain.android.dcc.modules.garden

import android.os.Bundle
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.App
import io.wexchain.dcc.R

/**
 *Created by liuyang on 2018/11/2.
 */
class GardenTaskActivity : BaseCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gardentask)
        initToolbar()
        initData()
    }

    private fun initData() {
        App.get().marketingApi.getTaskList(App.get().gardenTokenManager.gardenToken)
                .subscribeBy {

        }
    }
}