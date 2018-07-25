package io.wexchain.android.localprotect.fragment

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wexchain.android.common.fingerPrintAvailable
import io.wexchain.android.common.fingerPrintEnrolled
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.R
import io.wexchain.android.localprotect.databinding.FragmentChooseProtectMethodBinding

/**
 * Created by lulingzhi on 2017/11/21.
 */

class ChooseProtectMethodFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentChooseProtectMethodBinding

    val contentLayoutId: Int = R.layout.fragment_choose_protect_method

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,contentLayoutId,container,false)
        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSelectFingerPrint.setOnClickListener(this)
        binding.tvSelectGesture.setOnClickListener(this)
        binding.tvSelectPin.setOnClickListener(this)
        binding.tvCancel.setOnClickListener(this)
        binding.cancelText = this.cancelText
        val context = context!!
        binding.fingerPrintAvailable = context.fingerPrintAvailable()
        binding.fingerPrintEnrolled = context.fingerPrintEnrolled()
    }

    override fun onClick(v: View?) {
        v ?: return
        if (v.id == R.id.tv_cancel) {
            selectListener?.onCancel()
        } else {
            when (v.id) {
                R.id.tv_select_finger_print -> LocalProtectType.FINGER_PRINT
                R.id.tv_select_gesture -> LocalProtectType.GESTURE
                R.id.tv_select_pin -> LocalProtectType.PIN
                else -> null
            }?.let {
                selectListener?.onSelected(it)
            }
        }
    }

    var selectListener: CreateProtectSelectListener? = null
    var cancelText: String = getString(R.string.cancel)

    interface CreateProtectSelectListener {
        fun onSelected(type: LocalProtectType)
        fun onCancel()
    }

    companion object {
        fun create(selectListener: CreateProtectSelectListener, cancelText: String? = null): ChooseProtectMethodFragment {
            val fragment = ChooseProtectMethodFragment()
            fragment.selectListener = selectListener
            cancelText?.let { fragment.cancelText = it }
            return fragment
        }
    }
}