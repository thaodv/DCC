package io.wexchain.android.dcc.modules.passport

import android.arch.lifecycle.Observer
import android.content.ClipData
import android.os.Bundle
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPassportAddressBinding

class PassportAddressActivity : BindActivity<ActivityPassportAddressBinding>() {

    override val contentLayoutId: Int = R.layout.activity_passport_address

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.ivClose.onClick {
            finish()
        }

        //initToolbar(true)
        initView()
        initData()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        super.onCreateOptionsMenu(menu)
//        menuInflater.inflate(R.menu.menu_share_send,menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        item?:return super.onOptionsItemSelected(item)
//        when(item.itemId){
//            R.id.action_share_send -> {
//                binding.address?.let {
//                    startActivity(Intent(Intent.ACTION_SEND).apply {
//                        putExtra(Intent.EXTRA_TEXT,it)
//                        type = "text/plain"
//                    })
//                }
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun initView() {
        binding.btnCopy.onClick {
            binding.address?.let {
                getClipboardManager().primaryClip = ClipData.newPlainText("passport address", it)
                toast(R.string.copy_succeed)
            }
        }
        /*binding.tvAddress.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        binding.tvAddress.setOnClickListener {
            startActivity(StaticHtmlActivity.getResultIntent(this@PassportAddressActivity, "Searchain数据分析", Extras.Searchain + App.get().passportRepository.currPassport.value!!.address))
        }*/
    }

    private fun initData() {
        App.get().passportRepository.currPassport
                .observe(this, Observer {
                    binding.address = it?.address
                })
    }
}
