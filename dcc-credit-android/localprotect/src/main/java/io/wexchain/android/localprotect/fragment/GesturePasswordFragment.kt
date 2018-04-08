package io.wexchain.android.localprotect.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wexchain.android.localprotect.R
import io.wexchain.android.localprotect.VerifyMode
import io.wexchain.android.localprotect.view.LockPatternView
import kotlinx.android.synthetic.main.fragment_gesture_password.*

/**
 * Created by lulingzhi on 2017/11/22.
 */
class GesturePasswordFragment : Fragment(), LockPatternView.LockPatternChecker {
    override fun onCompleteInput(input: IntArray?) {
        listener?.onGestureSuccess(input!!.toGesturePasswordString(), currentMode)
    }

    override fun checkInput(input: IntArray?): Boolean {
        return if (input == null || input.size < 4) {
            this.tv_error_hint.setText(R.string.notice_gesture_at_least_4)
            false
        } else if (verifyGesture != null && verifyGesture != input.toGesturePasswordString()) {
            when (currentMode) {
                VerifyMode.INPUT_NEW -> {
                }
                VerifyMode.INPUT_REPEAT -> {
                    this.tv_error_hint.setText(R.string.gesture_password_not_match_please_retry)
                }
                VerifyMode.VERIFY -> {
                    this.tv_error_hint.setText(R.string.gesture_password_error_please_retry)
                }
            }
            false
        } else {
            this.tv_error_hint.text = null
            true
        }
    }

    private fun IntArray.toGesturePasswordString(): String {
        return this.joinToString(" ", transform = { it.toString() })
    }

    private lateinit var gestureView: LockPatternView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gesture_password, container, false)
        gestureView = view.findViewById(R.id.lpv_gesture)
        gestureView.setPatternChecker(this)
        view.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            listener?.onGestureCancel(currentMode)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        setTextForMode(currentMode)
    }

    private fun setTextForMode(mode: VerifyMode) {
        when (mode) {
            VerifyMode.INPUT_NEW -> {
                this.tv_title.setText(R.string.enable_gesture_pattern)
                this.tv_hint.setText(R.string.please_draw_pattern)
            }
            VerifyMode.INPUT_REPEAT -> {
                this.tv_title.setText(R.string.enable_gesture_pattern)
                this.tv_hint.setText(R.string.please_draw_pattern_again)
            }
            VerifyMode.VERIFY -> {
                this.tv_title.setText(R.string.verify_gesture_pattern)
                this.tv_hint.text = null
            }
        }
    }

    var currentMode = VerifyMode.VERIFY
    var verifyGesture: String? = null

    interface Listener {
        fun onGestureSuccess(gesture: String, mode: VerifyMode)
        fun onGestureCancel(mode: VerifyMode)
    }

    var listener: Listener? = null

    companion object {
        fun createNew(listener: Listener): GesturePasswordFragment {
            val fragment = GesturePasswordFragment()
            fragment.currentMode = VerifyMode.INPUT_NEW
            fragment.listener = listener
            return fragment
        }

        fun createVerify(gesture: String, listener: Listener): GesturePasswordFragment {
            val fragment = GesturePasswordFragment()
            fragment.currentMode = VerifyMode.VERIFY
            fragment.verifyGesture = gesture
            fragment.listener = listener
            return fragment
        }

        fun createRepeat(gesture: String, listener: Listener): GesturePasswordFragment {
            val fragment = GesturePasswordFragment()
            fragment.currentMode = VerifyMode.INPUT_REPEAT
            fragment.verifyGesture = gesture
            fragment.listener = listener
            return fragment
        }
    }
}