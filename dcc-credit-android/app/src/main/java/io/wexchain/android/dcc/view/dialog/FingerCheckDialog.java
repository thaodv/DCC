package io.wexchain.android.dcc.view.dialog;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import javax.crypto.Cipher;

import io.wexchain.android.common.base.BaseCompatActivity;
import io.wexchain.dcc.R;

/**
 * @author Created by Wangpeng on 2018/7/16 20:04.
 * usage:
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerCheckDialog extends DialogFragment {

    private FingerprintManager fingerprintManager;
    private CancellationSignal mCancellationSignal;

    private Cipher mCipher;
    private BaseCompatActivity mActivity;

    public TextView errorMsg;
    public Button mBtCancel;

    /**
     * 标识是否是用户主动取消的认证。
     */
    private boolean isSelfCancelled;

    public void setCipher(Cipher cipher) {
        mCipher = cipher;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseCompatActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fingerprintManager = getContext().getSystemService(FingerprintManager.class);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.deleteAddressBookDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_finger_check, container, false);
        errorMsg = v.findViewById(R.id.tv_error_msg);
        mBtCancel = v.findViewById(R.id.bt_cancel);
        mBtCancel.setOnClickListener(v1 -> {
            dismiss();
            mOnClickListener.cancel();
            stopListening();
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume(); // 开始指纹认证监听
        startListening(mCipher);
    }

    @Override
    public void onPause() {
        super.onPause(); // 停止指纹认证监听
        stopListening();
    }

    public interface OnClickListener {
        void cancel();
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }

    private void startListening(Cipher cipher) {
        isSelfCancelled = false;
        mCancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), mCancellationSignal,
                0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                if (!isSelfCancelled) {
                    errorMsg.setText(errString);
                    if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                        Toast.makeText(mActivity, errString, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                errorMsg.setText(helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                Toast.makeText(mActivity, "指纹认证成功", Toast.LENGTH_SHORT).show();
                mOnCallBack.onAuthenticated();
                FingerCheckDialog.this.dismiss();
            }

            @Override
            public void onAuthenticationFailed() {
                errorMsg.setText("指纹认证失败，请再试一次");
            }
        }, null);
    }

    private void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
            isSelfCancelled = true;
        }
    }

    private onCallBack mOnCallBack;

    public void setOnCallBack(onCallBack onCallBack) {
        mOnCallBack = onCallBack;
    }

    public interface onCallBack {
        void onAuthenticated();
    }

}
