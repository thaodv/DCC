package io.wexchain.android.dcc.fragment

import android.content.ClipData
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AlignmentSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.setInterceptScroll
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BaseCompatFragment
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.view.dialog.CustomDialog
import io.wexchain.android.dcc.view.dialog.FullScreenDialog
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentExportKeystoreBinding


/**
 * Created by lulingzhi on 2017/11/17.
 */
class ExportKeystoreFragment : BaseCompatFragment() {

    private lateinit var binding :FragmentExportKeystoreBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_export_keystore,container,false)
        initViewModel()
        binding.tvKeystore.setInterceptScroll()
        return binding.root
    }

    private fun initViewModel() {
        binding.wallet = App.get().passportRepository.getWallet()
        binding.btnCopy.setOnClickListener {
            val data = binding.wallet
            data?.let {
                context!!.getClipboardManager().primaryClip = ClipData.newPlainText("KeyStore", it)
                toast(R.string.copy_succeed)
            }
        }
        binding.btnPeekPassword.setOnClickListener {
            tryUnlockAndShowPassword()
        }
        binding.ivQrCode.setOnClickListener {
            showQrInFullScreen(binding.wallet)
        }
    }

    private fun showQrInFullScreen(content: String?) {
        content?:return
        FullScreenDialog.createQr(context!!, content).show()
    }

    private fun tryUnlockAndShowPassword() {
        //style
        VerifyProtectFragment.create(object : VerifyProtectFragment.Listener {
            override fun verifyCancelledNotSucceed() {

            }

            override fun onVerifySuccess() {
                val password = App.get().passportRepository.getPassword()
                val context = context!!
                CustomDialog(context)
                        .apply {
                            setTitle("钱包密码")
                            textContent = password.centered()
                            withPositiveButton("知道了")
                        }
                        .assembleAndShow()
            }
        }).show(childFragmentManager,null)
    }

    companion object {
        const val ARG_PASSPORT = "passport"
        fun create(passport:Passport): ExportKeystoreFragment {
            val fragment = ExportKeystoreFragment()
            return fragment
        }

        fun CharSequence.centered(): CharSequence {
            val spannableStringBuilder = SpannableStringBuilder(this)
            spannableStringBuilder.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, this.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            return spannableStringBuilder
        }
    }
}