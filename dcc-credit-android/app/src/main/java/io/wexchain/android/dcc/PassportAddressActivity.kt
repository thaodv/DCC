package io.wexchain.android.dcc

import android.arch.lifecycle.Observer
import android.content.ClipData
import android.os.Bundle
import com.wexmarket.android.passport.base.BindActivity
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.toast
import io.wexchain.auth.R
import io.wexchain.auth.databinding.ActivityPassportAddressBinding

class PassportAddressActivity : BindActivity<ActivityPassportAddressBinding>() {
    override val contentLayoutId: Int = R.layout.activity_passport_address

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(true)
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
        binding.btnCopy.setOnClickListener {
            binding.address?.let {
                getClipboardManager().primaryClip = ClipData.newPlainText("passport address",it)
                toast(R.string.copy_succeed)
            }
        }
    }

    private fun initData() {
        App.get().passportRepository.currPassport
                .observe(this, Observer {
                    binding.address = it?.address
                })
    }
}
