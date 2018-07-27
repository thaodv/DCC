package io.wexchain.android.localprotect.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.*
import io.wexchain.android.common.toast
import io.wexchain.android.localprotect.*

/**
 * Created by lulingzhi on 2017/11/28.
 */
class VerifyProtectFragment : DialogFragment(), GesturePasswordFragment.Listener, PinFragment.Listener, FingerPrintFragment.Listener {
    override fun onFingerPrintCancelled() {
        dismiss()//todo
    }

    override fun onFingerPrintInvalidated() {
        //todo
        LocalProtect.disableProtect()
//        dismissAndEmitSuccess()
        dismiss()
        showReOpenProtectDialog()
    }

    override fun onFingerPrintVerified() {
        dismissAndEmitSuccess()
    }

    override fun onPinCancel(mode: VerifyMode) {
        dismiss()//todo
    }

    override fun onPinSuccess(pin: String, mode: VerifyMode) {
        dismissAndEmitSuccess()
    }

    override fun onGestureCancel(mode: VerifyMode) {
        dismiss()//todo
    }

    override fun onGestureSuccess(gesture: String, mode: VerifyMode) {
        dismissAndEmitSuccess()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullWidthDialog_SlideRight)
    }

    private var verifiedSuccess = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_step_container, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window.apply {
            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        verifiedSuccess = false
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        emitCancelOrFail()
    }

    private fun emitCancelOrFail() {
        if (!verifiedSuccess) {
            listener?.verifyCancelledNotSucceed()
        }
    }

    override fun onStart() {
        super.onStart()
        showVerifyChallengeOrEmitSuccess()
    }

    private fun showReOpenProtectDialog() {
        val activity = activity!!
        AlertDialog.Builder(activity)
                .setTitle("开启本地安全保护")
                .setMessage("指纹密码已被关闭,请您重新开启本地安全保护")
                .setPositiveButton("开启",DialogInterface.OnClickListener { _, _ ->
                    showCreateProtect(activity)
                })
                .setNegativeButton(R.string.cancel,null)
                .show()
    }

    private fun showCreateProtect(activity: FragmentActivity) {
        CreateProtectFragment.create(object : CreateProtectFragment.CreateProtectFinishListener {
            override fun onCancel(): Boolean {
                return true
            }

            override fun cancelText(): String {
                return getString(R.string.cancel)
            }

            override fun onCreateProtectFinish(type: LocalProtectType) {
                toast(R.string.local_protect_enabled, activity)
            }

        }).show(activity.supportFragmentManager, null)
    }

    private fun showVerifyChallengeOrEmitSuccess() {
        val protect = LocalProtect.currentProtect.value
        if (protect == null) {
            dismissAndEmitSuccess()
        } else {
            val param = protect.second!!
            when (protect.first) {
                LocalProtectType.FINGER_PRINT -> {
                    showFragment(FingerPrintFragment.createVerify(param, this))
                }
                LocalProtectType.GESTURE -> {
                    showFragment(GesturePasswordFragment.createVerify(param, this))
                }
                LocalProtectType.PIN -> {
                    showFragment(PinFragment.createVerify(param, this))
                }
            }
        }
    }

    private fun dismissAndEmitSuccess() {
        verifiedSuccess = true
        dismiss()
        listener?.onVerifySuccess()
    }

    private fun showFragment(fragment: Fragment) {
        val tx = childFragmentManager.beginTransaction()
        tx.replace(R.id.fl_container, fragment)
        tx.commit()
    }

    var listener: Listener? = null

    interface Listener {
        fun onVerifySuccess()
        fun verifyCancelledNotSucceed()
    }

    companion object {
        fun create(listener: Listener): VerifyProtectFragment {
            val fragment = VerifyProtectFragment()
            fragment.listener = listener
            return fragment
        }

        fun serve(useProtect: UseProtect, owner: LifecycleOwner, fragmentManager: () -> FragmentManager) {
            useProtect.protectChallengeEvent.observe(owner, Observer { next ->
                next ?: return@Observer
                val listener: Listener = object : Listener {
                    override fun onVerifySuccess() {
                        next.invoke(true)
                    }

                    override fun verifyCancelledNotSucceed() {
                        next.invoke(false)
                    }
                }
                create(listener).show(fragmentManager(), null)
            })
        }

        fun serve(useProtect: UseProtect, activity: AppCompatActivity) {
            serve(useProtect, activity, { activity.supportFragmentManager })
        }
    }
}