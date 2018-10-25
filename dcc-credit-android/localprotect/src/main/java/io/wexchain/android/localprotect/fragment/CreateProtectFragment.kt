package io.wexchain.android.localprotect.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.*
import io.wexchain.android.common.tools.AESSign
import io.wexchain.android.common.tools.CommonUtils
import io.wexchain.android.localprotect.FingerPrintHelper.generateProtectKeyPair
import io.wexchain.android.localprotect.LocalProtect
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.R
import io.wexchain.android.localprotect.VerifyMode

/**
 * Created by lulingzhi on 2017/11/21.
 * Create ec private key local protect fragment dialog
 */
class CreateProtectFragment : DialogFragment(),
        DialogInterface.OnShowListener,
        ChooseProtectMethodFragment.CreateProtectSelectListener,
        GesturePasswordFragment.Listener, PinFragment.Listener {
    override fun onPinCancel(mode: VerifyMode) {
        dismiss()//todo
    }

    override fun onPinSuccess(pin: String, mode: VerifyMode) {
        val type = LocalProtectType.PIN
        when (mode) {
            VerifyMode.INPUT_NEW -> {
                showProgress(PinFragment.createRepeat(pin, this), "$type-step2")
            }
            VerifyMode.INPUT_REPEAT -> {
                onProtectSuccessfullySet(type, pin)
            }
            VerifyMode.VERIFY -> {
                // can't happen
            }
        }
    }

    override fun onGestureCancel(mode: VerifyMode) {
        dismiss()//todo
    }

    override fun onGestureSuccess(gesture: String, mode: VerifyMode) {
        when (mode) {
            VerifyMode.INPUT_NEW -> {
                showProgress(GesturePasswordFragment.createRepeat(gesture, this), "${LocalProtectType.GESTURE}-step2")
            }
            VerifyMode.INPUT_REPEAT -> onProtectSuccessfullySet(LocalProtectType.GESTURE, gesture)
            VerifyMode.VERIFY -> {
                //can't happen
            }
        }
    }

    private fun onProtectSuccessfullySet(type: LocalProtectType, param: String) {
        dismiss()

        var second: String

        if (LocalProtectType.FINGER_PRINT == type) {
            second = param
        } else {
            second = AESSign.encryptPsw(param, CommonUtils.getMacAddress())
        }

        LocalProtect.setProtect(type, second)
        finishListener?.onCreateProtectFinish(type)
    }

    override fun onCancel() {
        dismiss()
        finishListener?.onCancel()
    }

    override fun onSelected(type: LocalProtectType) {
        val name = "$type-step1"
        when (type) {
            LocalProtectType.FINGER_PRINT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    enableFingerPrint()
                }
            }
            LocalProtectType.GESTURE -> showProgress(GesturePasswordFragment.createNew(this), name)
            LocalProtectType.PIN -> showProgress(PinFragment.createNew(this), name)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun enableFingerPrint() {
        val pubKey = generateProtectKeyPair()
        LocalProtect.setProtect(LocalProtectType.FINGER_PRINT, pubKey)
        dismiss()
        finishListener?.onCreateProtectFinish(LocalProtectType.FINGER_PRINT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullWidthDialog_SlideRight)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_step_container, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener(this)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window.apply {
            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        return dialog
    }

    override fun onShow(dialog: DialogInterface?) {
        //clear back stack
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        showProgress(ChooseProtectMethodFragment.create(this, finishListener?.cancelText()), "choose", false)
    }

    private fun showProgress(fragment: Fragment, name: String, animate: Boolean = true) {
        val tx = childFragmentManager.beginTransaction()
        if (animate) {
            tx.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        tx.replace(R.id.fl_container, fragment)
        tx.addToBackStack(name)
        tx.commit()
    }

    var finishListener: CreateProtectFinishListener? = null

    interface CreateProtectFinishListener {
        fun onCreateProtectFinish(type: LocalProtectType)
        /**
         * @return true for dismiss
         */
        fun onCancel(): Boolean

        fun cancelText(): String
    }

    companion object {
        fun create(finishListener: CreateProtectFinishListener): CreateProtectFragment {
            val fragment = CreateProtectFragment()
            fragment.finishListener = finishListener
            return fragment
        }
    }
}
