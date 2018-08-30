package io.wexchain.android.dcc.modules.ipfs.activity

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.R
import io.wexchain.ipfs.core.IpfsCore
import kotlinx.android.synthetic.main.activity_select_node.*

/**
 *Created by liuyang on 2018/8/27.
 */
class SelectNodeActivity : BaseCompatActivity() {

    private val passport by lazy {
        App.get().passportRepository
    }

    private val cm by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private var host: String? = null
    private var port: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_node)
        initToolbar()
        initClick()
    }

    override fun onResume() {
        super.onResume()
        checkUrlStatus()
    }

    private fun checkUrlStatus() {
        val urlConfig = passport.getIpfsUrlConfig()
        val hostStatus = passport.getIpfsHostStatus()
        if (TextUtils.isEmpty(urlConfig.first) || TextUtils.isEmpty(urlConfig.second)) {
            default_tag.isClickable = false
            custom_tag.isClickable = false
            custom_host.text = "尚未配置"
        } else {
            default_tag.isClickable = true
            custom_tag.isClickable = true
            host = urlConfig.first
            port = urlConfig.second
            custom_host.text = "${urlConfig.first} 端口${urlConfig.second}"
            if (hostStatus) {
                custom_img.visibility = View.INVISIBLE
                default_img.visibility = View.VISIBLE
            } else {
                custom_img.visibility = View.VISIBLE
                default_img.visibility = View.INVISIBLE
            }
        }
    }

    private fun initClick() {
        edit_node.onClick {
            navigateTo(EditNodeActivity::class.java)
        }

        default_tag.onClick {
            passport.setIpfsHostStatus(true)
            custom_img.visibility = View.INVISIBLE
            default_img.visibility = View.VISIBLE
            IpfsCore.init(BuildConfig.IPFS_HOST)
        }

        custom_tag.onClick {
            passport.setIpfsHostStatus(false)
            custom_img.visibility = View.VISIBLE
            default_img.visibility = View.INVISIBLE
            IpfsCore.init(host!!, port!!.toInt())
        }

        ipfs_node_tip.onClick {
            cm.text = "https://open.dcc.finance"
            toast("网址复制成功")
        }
    }
}