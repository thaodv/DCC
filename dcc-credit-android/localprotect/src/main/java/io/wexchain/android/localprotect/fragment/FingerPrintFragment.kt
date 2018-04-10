package io.wexchain.android.localprotect.fragment

import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import io.wexchain.android.common.fingerPrintAvailable
import io.wexchain.android.common.fingerPrintEnrolled
import io.wexchain.android.common.getFingerPrintManager
import io.wexchain.android.localprotect.FingerPrintHelper.initProtectCryptoObject
import io.wexchain.android.localprotect.FingerPrintHelper.verifySuite
import io.wexchain.android.localprotect.R
import java.security.Signature

/**
 * Created by lulingzhi on 2017/11/28.
 */
@RequiresApi(Build.VERSION_CODES.M)
class FingerPrintFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_finger_print, container, false)
        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            listener?.onFingerPrintCancelled()
        }
        return view
    }

    private val authenticationCallback = object : FingerprintManager.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            tryVerify(result.cryptoObject.signature)
        }
    }

    private fun tryVerify(signature: Signature?) {
        //todo
        signature ?: return
        val p = pubKey
        p ?: return
        if (verifySuite(signature, p)) {
            listener?.onFingerPrintVerified()
        }
    }

    private var cryptoObject: FingerprintManager.CryptoObject? = null
    private var pubKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val co = prepareCryptoObject()
        if (co == null) {
            listener?.onFingerPrintInvalidated()
        } else {
            this.cryptoObject = co
        }
    }

    private fun prepareCryptoObject(): FingerprintManager.CryptoObject? {
        val ctx = context!!
        return if (ctx.fingerPrintAvailable() && ctx.fingerPrintEnrolled()) {
            initProtectCryptoObject()
        } else null
    }

    override fun onResume() {
        super.onResume()
        startFingerPrintListening()
    }

    override fun onPause() {
        super.onPause()
        stopFingerPrintListening()
    }

    private var cancellationSignal: CancellationSignal? = null

    private fun stopFingerPrintListening() {
        cancellationSignal?.cancel()
        cancellationSignal = null
    }

    private fun startFingerPrintListening() {
        cryptoObject?.let {
            val signal = CancellationSignal()
            context!!.getFingerPrintManager().authenticate(it, signal, 0, authenticationCallback, null)
            cancellationSignal = signal
        }
    }

    private var listener: Listener? = null

    interface Listener {
        fun onFingerPrintVerified()
        fun onFingerPrintInvalidated()
        fun onFingerPrintCancelled()
    }

    companion object {

        fun createVerify(pubKey: String, listener: Listener): FingerPrintFragment {
            val fragment = FingerPrintFragment()
            fragment.listener = listener
            fragment.pubKey = pubKey
            return fragment
        }
    }
}