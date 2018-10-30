package io.wexchain.android.localprotect.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wexchain.android.localprotect.R
import io.wexchain.android.localprotect.VerifyMode
import io.wexchain.android.localprotect.databinding.FragmentPinBinding
import io.wexchain.android.localprotect.vm.PinVm

/**
 * Created by lulingzhi on 2017/11/28.
 */
class PinFragment : Fragment() {

    private lateinit var binding: FragmentPinBinding

    val contentLayoutId: Int = R.layout.fragment_pin

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, contentLayoutId, container, false)
        return binding.root
    }

    private var currentMode = VerifyMode.VERIFY
    private var verifyPin: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pinVm = ViewModelProviders.of(this).get(PinVm::class.java)
        pinVm.clear()
        pinVm.pinCompleteEvent.observe(this, Observer {
            it?.let {
                checkPin(it)
            }
        })
        binding.pin = pinVm
        binding.btnCancel.setOnClickListener {
            listener?.onPinCancel(currentMode)
        }
        binding.pin!!.clearHit = {
            if (!binding.tvErrorHint.text.isNullOrEmpty()){
                binding.tvErrorHint.text = null
            }
        }
    }

    fun String.checkWeak(): Boolean {
        return equalStr() || isOrderNumeric() || isOrderNumeric_()
    }

    fun String.equalStr(): Boolean {
        var flag = true
        val str = this[0]
        for (i in 0 until this.length) {
            if (str != this[i]) {
                flag = false
                break
            }
        }
        return flag
    }

    fun String.isOrderNumeric(): Boolean {
        var flag = true//如果全是连续数字返回true
        var isNumeric = true//如果全是数字返回true
        for (i in 0 until this.length) {
            if (!Character.isDigit(this[i])) {
                isNumeric = false
                break
            }
        }
        if (isNumeric) {//如果全是数字则执行是否连续数字判断
            for (i in 0 until this.length) {
                if (i > 0) {//判断如123456
                    val num = Integer.parseInt(this[i] + "")
                    val num_ = Integer.parseInt(this[i - 1] + "") + 1
                    if (num != num_) {
                        flag = false
                        break
                    }
                }
            }
        } else {
            flag = false
        }
        return flag
    }

    //不能是连续的数字--递减（如：987654、876543）连续数字返回true
    fun String.isOrderNumeric_(): Boolean {
        var flag = true//如果全是连续数字返回true
        var isNumeric = true//如果全是数字返回true
        for (i in 0 until this.length) {
            if (!Character.isDigit(this[i])) {
                isNumeric = false
                break
            }
        }
        if (isNumeric) {//如果全是数字则执行是否连续数字判断
            for (i in 0 until this.length) {
                if (i > 0) {//判断如654321
                    val num = Integer.parseInt(this[i] + "")
                    val num_ = Integer.parseInt(this[i - 1] + "") - 1
                    if (num != num_) {
                        flag = false
                        break
                    }
                }
            }
        } else {
            flag = false
        }
        return flag
    }


    private fun checkPin(input: String) {
        when (currentMode) {
            VerifyMode.INPUT_NEW -> {
                if (input.length == 6) {
                    if (input.checkWeak()) {
                        binding.pin?.pin?.set("")
                        binding.tvErrorHint.text = "输入密码过于简单,请重新输入"
                    } else {
                        binding.tvErrorHint.text = null
                        listener?.onPinSuccess(input, currentMode)
                    }
                }
            }
            VerifyMode.INPUT_REPEAT -> {
                val v = verifyPin
                if (v != null && v == input) {
                    binding.tvErrorHint.text = null
                    listener?.onPinSuccess(input, currentMode)
                } else {
                    binding.tvErrorHint.setText(R.string.pin_repeat_not_match)
                }
            }
            VerifyMode.VERIFY -> {
                val v = verifyPin
                if (v != null && v == input) {
                    listener?.onPinSuccess(input, currentMode)
                } else {
                    binding.tvErrorHint.setText(R.string.pin_incorrect_please_retry)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setTextForMode(currentMode)
    }

    private fun setTextForMode(mode: VerifyMode) {
        when (mode) {
            VerifyMode.INPUT_NEW -> {
                binding.tvTitle.setText(R.string.enable_pin)
            }
            VerifyMode.INPUT_REPEAT -> {
                binding.tvTitle.setText(R.string.repeat_pin)
            }
            VerifyMode.VERIFY -> {
                binding.tvTitle.setText(R.string.verify_pin)
            }
        }
    }

    private var listener: Listener? = null

    interface Listener {
        fun onPinSuccess(pin: String, mode: VerifyMode)
        fun onPinCancel(mode: VerifyMode)
    }

    companion object {
        fun createNew(listener: Listener): PinFragment {
            val fragment = PinFragment()
            fragment.listener = listener
            fragment.currentMode = VerifyMode.INPUT_NEW
            return fragment
        }

        fun createVerify(pin: String, listener: Listener): PinFragment {
            val fragment = PinFragment()
            fragment.listener = listener
            fragment.currentMode = VerifyMode.VERIFY
            fragment.verifyPin = pin
            return fragment
        }

        fun createRepeat(pin: String, listener: Listener): PinFragment {
            val fragment = PinFragment()
            fragment.listener = listener
            fragment.currentMode = VerifyMode.INPUT_REPEAT
            fragment.verifyPin = pin
            return fragment
        }
    }
}
