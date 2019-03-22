package io.wexchain.android.dcc.view.dialog.qrcode;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import io.wexchain.dcc.R;

/**
 * @author Created by Wangpeng on 2018/7/16 20:04.
 * usage:
 */
public class QScanResultIsAddressDialog extends Dialog implements View.OnClickListener {
    
    public TextView mTvAddress;
    public TextView mTvTrustTransfer;
    public TextView mTvDigestTransfer;
    public TextView mTvTrustWithdraw;
    public TextView mTvCancel;
    
    
    public QScanResultIsAddressDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_qscan_result_is_address);
        
        getWindow().setBackgroundDrawableResource(R.color.white);
        
        mTvAddress = findViewById(R.id.tv_address);
        mTvTrustTransfer = findViewById(R.id.tv_trust_transfer);
        mTvDigestTransfer = findViewById(R.id.tv_digest_transfer);
        mTvTrustWithdraw = findViewById(R.id.tv_trust_withdraw);
        mTvCancel = findViewById(R.id.tv_cancle);
        
        mTvTrustTransfer.setOnClickListener(this);
        mTvDigestTransfer.setOnClickListener(this);
        mTvTrustWithdraw.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }
    
    public void setParameter(String address) {
        mTvAddress.setText(address);
    }
    
    public void isTrust(boolean isTrust) {
        if (isTrust) {
            mTvTrustTransfer.setVisibility(View.VISIBLE);
        } else {
            mTvDigestTransfer.setVisibility(View.VISIBLE);
            mTvTrustWithdraw.setVisibility(View.VISIBLE);
        }
    }
    
    public void isBtc(boolean isBtc) {
        if (isBtc) {
            mTvTrustWithdraw.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_trust_transfer:
                mOnClickListener.trustTransfer();
                dismiss();
                break;
            case R.id.tv_digest_transfer:
                mOnClickListener.digestTransfer();
                dismiss();
                break;
            case R.id.tv_trust_withdraw:
                mOnClickListener.trustWithdraw();
                dismiss();
                break;
            case R.id.tv_cancle:
                dismiss();
                break;
            default:
                break;
        }
    }
    
    public interface OnClickListener {
        void trustTransfer();
        
        void digestTransfer();
        
        void trustWithdraw();
    }
    
    private OnClickListener mOnClickListener;
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
    
    
}
