package io.wexchain.android.dcc.fragment.ewallet

import android.content.ClipData
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wexchain.android.common.base.BaseCompatFragment
import io.wexchain.android.common.getClipboardManager
import io.wexchain.android.common.setInterceptScroll
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.tools.onLongSaveImageToGallery
import io.wexchain.android.dcc.view.dialog.FullScreenDialog
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentExportPrivateKeyBinding
import io.wexchain.dccchainservice.DccChainServiceException

/**
 * Created by lulingzhi on 2017/11/17.
 */
class ExportPrivateKeyFragment : BaseCompatFragment() {

    private val privateKey by lazy { arguments!!.getString(ARG_PRIVATE_KEY)!! }

    private lateinit var binding: FragmentExportPrivateKeyBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_export_private_key, container, false)
        binding.privateKey = this.privateKey
        binding.btnCopy.setOnClickListener {
            binding.privateKey?.let {
                context!!.getClipboardManager().primaryClip = ClipData.newPlainText("PrivateKey", binding.privateKey)
                toast(R.string.copy_succeed)
            }
        }
        binding.ivQrCode.setOnClickListener {
            showQrInFullScreen(binding.privateKey)
        }
        binding.ivQrCode.onLongSaveImageToGallery(
                onError = {
                    toast(if (it is DccChainServiceException)
                        it.message!!
                    else "二维码保存失败")
                },
                onSuccess = {
                    toast("二维码已保存至: $it")
                })
        binding.tvPrivateKey.setInterceptScroll()
        return binding.root
    }

    private fun showQrInFullScreen(content: String?) {
        content ?: return
        FullScreenDialog.createQr(context!!, content).show()
    }

    companion object {
        const val ARG_PRIVATE_KEY = "private_key"

        fun create(privateKey: String): ExportPrivateKeyFragment {
            val fragment = ExportPrivateKeyFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_PRIVATE_KEY, privateKey)
            }
            return fragment
        }
    }
}