package worhavah.regloginlib.tools

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import wex.regloginlib.R

class ReLoginActivity : Activity() {


    lateinit var ivClosew: View
    lateinit var tvcontent: TextView
    lateinit var tv_updata: TextView
    lateinit var tv_cancel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding.tvContent.setText("")
        setContentView(R.layout.activity_relogin)
//窗口对齐屏幕宽度
        val win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        var layoutParams: WindowManager.LayoutParams = win.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.BOTTOM;//设置对话框在底部显示
        win.setAttributes(layoutParams);
        tv_updata = findViewById(R.id.tv_updata)
        tv_cancel = findViewById(R.id.tv_cancel)
        tv_cancel.setOnClickListener {
            finish()
        }
        tv_updata.setOnClickListener {
            ScfOperations. updateAuthKey( )
            finish()
        }


    }


}
