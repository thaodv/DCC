package io.wexchain.android.dcc.modules.selectnode

import android.os.Bundle
import com.wexmarket.android.network.Networking
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.tools.LogUtils
import io.wexchain.android.dcc.tools.doMain
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.api.EthJsonRpcApiWithAuth
import io.wexchain.digitalwallet.proxy.EthsRpcAgent

class SelectNodeActivity : BaseCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_node)

        val nodeList = App.get().nodeList

        val base1 = nodeList[0].url
        val base2 = nodeList[1].url
        val base3 = nodeList[2].url
        val base4 = nodeList[3].url

        LogUtils.i("SelectNodeActivity-base1-time-start",System.currentTimeMillis())
        LogUtils.i("SelectNodeActivity-base2-time-start",System.currentTimeMillis())
        LogUtils.i("SelectNodeActivity-base3-time-start",System.currentTimeMillis())
        LogUtils.i("SelectNodeActivity-base4-time-start",System.currentTimeMillis())

        val createApi = Networking(App.get()).createApi(EthJsonRpcApiWithAuth::class.java, base1)
        val agent = EthsRpcAgent.by(createApi)

        agent.checkNode().doMain().subscribe({
            LogUtils.i("SelectNodeActivity",it)
            LogUtils.i("SelectNodeActivity-base1-time-end",System.currentTimeMillis())
        }, {
            LogUtils.e(it)
        })

    }
}
