package io.wexchain.android.dcc.modules.selectnode

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import com.wexmarket.android.network.Networking
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.LogUtils
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.android.dcc.tools.doMain
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.api.EthJsonRpcApiWithAuth
import io.wexchain.digitalwallet.proxy.EthsRpcAgent

class SelectNodeActivity : BaseCompatActivity() {


    lateinit var grContent: RadioGroup
    lateinit var cb1: RadioButton
    lateinit var cb2: RadioButton
    lateinit var cb3: RadioButton
    lateinit var cb4: RadioButton

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_node)
        initToolbar(true)

        grContent = findViewById(R.id.gr_content)
        cb1 = findViewById(R.id.cb1)
        cb2 = findViewById(R.id.cb2)
        cb3 = findViewById(R.id.cb3)
        cb4 = findViewById(R.id.cb4)

        cb1.id = 0
        cb2.id = 1
        cb3.id = 2
        cb4.id = 3


        val nodeList = App.get().nodeList

        val base1 = nodeList[0].url
        val base2 = nodeList[1].url
        val base3 = nodeList[2].url
        val base4 = nodeList[3].url

        var base = ShareUtils.getString(Extras.SP_SELECTED_NODE, base1)

        var index = 1

        for (item in nodeList) {
            if (item.url == base) {
                index = item.id
            }
        }

        when (index) {
            1 -> cb1.isChecked = true
            2 -> cb2.isChecked = true
            3 -> cb3.isChecked = true
            4 -> cb4.isChecked = true
        }

        var green = resources.getDrawable(R.drawable.icon_node_green)
        green.setBounds(0, 0, green.minimumWidth, green.minimumHeight)
        var yellow = resources.getDrawable(R.drawable.icon_node_yellow)
        yellow.setBounds(0, 0, yellow.minimumWidth, yellow.minimumHeight)
        var red = resources.getDrawable(R.drawable.icon_node_red)
        red.setBounds(0, 0, red.minimumWidth, red.minimumHeight)

        val createApi1 = Networking(App.get()).createApi(EthJsonRpcApiWithAuth::class.java, base1)
        val agent1 = EthsRpcAgent.by(createApi1)

        val startTime1 = System.currentTimeMillis()
        LogUtils.i("SelectNodeActivity-base1-time-start", startTime1)
        agent1.checkNode().doMain().subscribe({
            LogUtils.i("SelectNodeActivity", it)
            LogUtils.i("SelectNodeActivity-base1-time-end", System.currentTimeMillis())

            var res: String
            if (System.currentTimeMillis() - startTime1 <= 300) {
                cb1.setCompoundDrawables(null, null, green, null)
                res = resources.getString(R.string.select_node_green)
            } else if (System.currentTimeMillis() - startTime1 <= 1000) {
                cb1.setCompoundDrawables(null, null, yellow, null)
                res = resources.getString(R.string.select_node_yellow)
            } else {
                cb1.setCompoundDrawables(null, null, red, null)
                res = resources.getString(R.string.select_node_red)
            }
            cb1.text = nodeList[0].name + "\n  " + res
        }, {
            LogUtils.e(it)
            cb1.setCompoundDrawables(null, null, red, null)
            cb1.text = nodeList[0].name + "\n  " + resources.getString(R.string.select_node_red)
        })

        val createApi2 = Networking(App.get()).createApi(EthJsonRpcApiWithAuth::class.java, base2)
        val agent2 = EthsRpcAgent.by(createApi2)

        val startTime2 = System.currentTimeMillis()
        LogUtils.i("SelectNodeActivity-base2-time-start", startTime2)
        agent2.checkNode().doMain().subscribe({
            LogUtils.i("SelectNodeActivity", it)
            LogUtils.i("SelectNodeActivity-base2-time-end", System.currentTimeMillis())

            var res: String
            if (System.currentTimeMillis() - startTime2 <= 300) {
                cb2.setCompoundDrawables(null, null, green, null)
                res = resources.getString(R.string.select_node_green)
            } else if (System.currentTimeMillis() - startTime2 <= 1000) {
                cb2.setCompoundDrawables(null, null, yellow, null)
                res = resources.getString(R.string.select_node_yellow)
            } else {
                cb2.setCompoundDrawables(null, null, red, null)
                res = resources.getString(R.string.select_node_red)
            }
            cb2.text = nodeList[1].name + "\n  " + res

        }, {
            LogUtils.e(it)
            cb2.setCompoundDrawables(null, null, red, null)
            cb2.text = nodeList[1].name + "\n  " + resources.getString(R.string.select_node_red)
        })
        val createApi3 = Networking(App.get()).createApi(EthJsonRpcApiWithAuth::class.java, base3)
        val agent3 = EthsRpcAgent.by(createApi3)

        val startTime3 = System.currentTimeMillis()
        LogUtils.i("SelectNodeActivity-base3-time-start", startTime3)
        agent3.checkNode().doMain().subscribe({
            LogUtils.i("SelectNodeActivity", it)
            LogUtils.i("SelectNodeActivity-base3-time-end", System.currentTimeMillis())

            var res: String
            if (System.currentTimeMillis() - startTime3 <= 300) {
                cb3.setCompoundDrawables(null, null, green, null)
                res = resources.getString(R.string.select_node_green)
            } else if (System.currentTimeMillis() - startTime3 <= 1000) {
                cb3.setCompoundDrawables(null, null, yellow, null)
                res = resources.getString(R.string.select_node_yellow)
            } else {
                cb3.setCompoundDrawables(null, null, red, null)
                res = resources.getString(R.string.select_node_red)
            }
            cb2.text = nodeList[2].name + "\n  " + res
        }, {
            LogUtils.e(it)
            cb3.setCompoundDrawables(null, null, red, null)
            cb3.text = nodeList[2].name + "\n  " + resources.getString(R.string.select_node_red)
        })
        val createApi4 = Networking(App.get()).createApi(EthJsonRpcApiWithAuth::class.java, base4)
        val agent4 = EthsRpcAgent.by(createApi4)

        val startTime4 = System.currentTimeMillis()
        LogUtils.i("SelectNodeActivity-base4-time-start", startTime4)
        agent4.checkNode().doMain().subscribe({
            LogUtils.i("SelectNodeActivity", it)
            LogUtils.i("SelectNodeActivity-base4-time-end", System.currentTimeMillis())

            var res: String
            if (System.currentTimeMillis() - startTime4 <= 300) {
                cb4.setCompoundDrawables(null, null, green, null)
                res = resources.getString(R.string.select_node_green)
            } else if (System.currentTimeMillis() - startTime4 <= 1000) {
                cb4.setCompoundDrawables(null, null, yellow, null)
                res = resources.getString(R.string.select_node_yellow)
            } else {
                cb4.setCompoundDrawables(null, null, red, null)
                res = resources.getString(R.string.select_node_red)
            }
            cb4.text = nodeList[3].name + "\n  " + res
        }, {
            LogUtils.e(it)
            cb4.setCompoundDrawables(null, null, red, null)
            cb4.text = nodeList[3].name + "\n  " + resources.getString(R.string.select_node_red)
        })

        cb1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                grContent.check(-1)
            }
        }
        cb2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                grContent.check(-1)
            }
        }
        cb3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                grContent.check(-1)
            }
        }
        cb4.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                grContent.check(-1)
            }
        }

        grContent.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                LogUtils.i("SelectNodeActivity-checkedId", checkedId)
                if (-1 == checkedId) run { return } else {
                    ShareUtils.setString(Extras.SP_SELECTED_NODE, nodeList[checkedId].url)
                }
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        App.get().initServices(App.get())
        App.get().initData(App.get())

    }
}
