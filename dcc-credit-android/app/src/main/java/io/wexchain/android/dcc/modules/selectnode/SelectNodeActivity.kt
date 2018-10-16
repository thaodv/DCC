package io.wexchain.android.dcc.modules.selectnode

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.RadioButton
import android.widget.RadioGroup
import com.wexmarket.android.network.Networking
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.tools.LogUtils
import io.wexchain.android.dcc.tools.ShareUtils
import io.wexchain.dcc.R
import io.wexchain.digitalwallet.api.EthJsonRpcApiWithAuth
import io.wexchain.digitalwallet.proxy.EthsRpcAgent
import io.wexchain.ipfs.utils.io_main

class SelectNodeActivity : BaseCompatActivity() {

    lateinit var grContent: RadioGroup
    lateinit var cb1: RadioButton
    lateinit var cb2: RadioButton
    lateinit var cb3: RadioButton
    lateinit var cb4: RadioButton

    private lateinit var green: Drawable
    private lateinit var yellow: Drawable
    private lateinit var red: Drawable

    private val nodeList: List<NodeBean>
        get() {
            val nodeList = App.get().nodeList
            return nodeList
        }

    var index: Int = 1

    lateinit var base1: String
    lateinit var base2: String
    lateinit var base3: String
    lateinit var base4: String

    var text1 = ""
    var text2 = ""
    var text3 = ""
    var text4 = ""

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

        base1 = nodeList[0].url
        base2 = nodeList[1].url
        base3 = nodeList[2].url
        base4 = nodeList[3].url

        initColor()

        connect()


        cb1.setOnClickListener {
            cb1.isChecked = true
            grContent.check(-1)
            connect()
        }
        cb2.setOnClickListener {
            cb2.isChecked = true
            grContent.check(-1)
            connect()
        }
        cb3.setOnClickListener {
            cb3.isChecked = true
            grContent.check(-1)
            connect()
        }
        cb4.setOnClickListener {
            cb4.isChecked = true
            grContent.check(-1)
            connect()
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

    private fun connect() {

        val base = ShareUtils.getString(Extras.SP_SELECTED_NODE, base1)

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

        if (cb1.isChecked) {
            text1 = "已连接"
            text2 = ""
            text3 = ""
            text4 = ""
        } else if (cb2.isChecked) {
            text1 = ""
            text2 = "已连接"
            text3 = ""
            text4 = ""
        } else if (cb3.isChecked) {
            text1 = ""
            text2 = ""
            text3 = "已连接"
            text4 = ""
        } else if (cb4.isChecked) {
            text1 = ""
            text2 = ""
            text3 = ""
            text4 = "已连接"
        }

        val createApi1 = Networking(App.get()).createApi(EthJsonRpcApiWithAuth::class.java, base1)
        val agent1 = EthsRpcAgent.by(createApi1)

        val startTime1 = System.currentTimeMillis()
        LogUtils.i("SelectNodeActivity-base1-time-start", startTime1)
        agent1.checkNode()
                .io_main()
                .subscribeBy(
                        onSuccess = {
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
                            res = text1 + res

                            cb1.text = spannableString(nodeList[0].name, res)

                        },
                        onError = {
                            cb1.setCompoundDrawables(null, null, red, null)
                            cb1.text = spannableString(nodeList[0].name, resources.getString(R.string.select_node_timeout))
                        })

        val createApi2 = Networking(App.get()).createApi(EthJsonRpcApiWithAuth::class.java, base2)
        val agent2 = EthsRpcAgent.by(createApi2)

        val startTime2 = System.currentTimeMillis()
        LogUtils.i("SelectNodeActivity-base2-time-start", startTime2)
        agent2.checkNode()
                .io_main()
                .subscribeBy(
                        onSuccess = {
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
                            res = text2 + res

                            cb2.text = spannableString(nodeList[1].name, res)
                        },
                        onError = {
                            cb2.setCompoundDrawables(null, null, red, null)
                            cb2.text = spannableString(nodeList[1].name, resources.getString(R.string.select_node_timeout))
                        })
        val createApi3 = Networking(App.get()).createApi(EthJsonRpcApiWithAuth::class.java, base3)
        val agent3 = EthsRpcAgent.by(createApi3)

        val startTime3 = System.currentTimeMillis()
        LogUtils.i("SelectNodeActivity-base3-time-start", startTime3)

        agent3.checkNode()
                .io_main()
                .subscribeBy(
                        onSuccess = {
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
                            res = text3 + res

                            cb3.text = spannableString(nodeList[2].name, res)
                        },
                        onError = {
                            cb3.setCompoundDrawables(null, null, red, null)
                            cb3.text = spannableString(nodeList[2].name, resources.getString(R.string.select_node_timeout))
                        })

        val createApi4 = Networking(App.get()).createApi(EthJsonRpcApiWithAuth::class.java, base4)
        val agent4 = EthsRpcAgent.by(createApi4)

        val startTime4 = System.currentTimeMillis()
        LogUtils.i("SelectNodeActivity-base4-time-start", startTime4)

        agent4.checkNode()
                .io_main()
                .subscribeBy(
                        onSuccess = {
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
                            res = text4 + res

                            cb4.text = spannableString(nodeList[3].name, res)
                        },
                        onError = {
                            cb4.setCompoundDrawables(null, null, red, null)
                            cb4.text = spannableString(nodeList[3].name, resources.getString(R.string.select_node_timeout))
                        })
    }

    private fun initColor() {
        green = resources.getDrawable(R.drawable.icon_node_green)
        green.setBounds(0, 0, green.minimumWidth, green.minimumHeight)
        yellow = resources.getDrawable(R.drawable.icon_node_yellow)
        yellow.setBounds(0, 0, yellow.minimumWidth, yellow.minimumHeight)
        red = resources.getDrawable(R.drawable.icon_node_red)
        red.setBounds(0, 0, red.minimumWidth, red.minimumHeight)
    }

    private fun spannableString(name: String, res: String): SpannableString {
        val cbtxt = "$name\n  $res"
        val spannableString = SpannableString(cbtxt)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#9B9B9B"))
        spannableString.setSpan(colorSpan, cbtxt.length - res.length, cbtxt.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return spannableString
    }

    override fun onDestroy() {
        super.onDestroy()
        App.get().initServices(App.get())
        App.get().initData(App.get())

    }
}
