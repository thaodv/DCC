package io.wexchain.android.dcc.modules.redpacket

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import io.wexchain.android.common.base.BaseCompatActivity
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.chain.GardenOperations
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.dcc.R

class RuleActivity : BaseCompatActivity() {

    private val mRuleText get() = intent.getStringExtra(Extras.EXTRA_REDPACKET_RULE_TEXT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule)
        initToolbar(true)

        this.findViewById<Button>(R.id.bt_invite).setOnClickListener {
            GardenOperations.shareWechatRedPacket {
                toast(it)
            }
        }
        this.findViewById<TextView>(R.id.tv_rule_text).text = mRuleText
    }
}
