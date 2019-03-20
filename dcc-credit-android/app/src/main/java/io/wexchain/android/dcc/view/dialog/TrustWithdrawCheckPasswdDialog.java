package io.wexchain.android.dcc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import io.wexchain.android.dcc.view.passwordview.PassWordLayout;
import io.wexchain.dcc.R;

/**
 * @author Created by Wangpeng on 2018/7/16 20:04.
 * usage:
 */
public class TrustWithdrawCheckPasswdDialog extends Dialog implements View.OnClickListener {
    
    public ImageButton mIbtClose;
    public TextView mTvForget;
    public PassWordLayout mPassword;
    
    
    public TrustWithdrawCheckPasswdDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_trust_withdraw_check_passwd);
        
        getWindow().setBackgroundDrawableResource(R.color.white);
        
        mIbtClose = findViewById(R.id.ibt_close);
        mTvForget = findViewById(R.id.tv_forget);
        mPassword = findViewById(R.id.password);
        
        mIbtClose.setOnClickListener(this);
        mTvForget.setOnClickListener(this);
        
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibt_close:
                dismiss();
                break;
            case R.id.tv_forget:
                mOnClickListener.forget();
                dismiss();
                break;
            default:
                break;
        }
    }
    
    public interface OnClickListener {
        void forget();
    }
    
    private OnClickListener mOnClickListener;
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
    
    
}
