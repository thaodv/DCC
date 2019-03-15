package io.wexchain.android.dcc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import io.wexchain.dcc.R;

/**
 * @author Created by Wangpeng on 2018/7/16 20:04.
 * usage:
 */
public class TrustTransferDialog extends Dialog implements View.OnClickListener {
    
    public ImageButton mIbtClose;
    public TextView mTvMobile;
    public TextView mTvAddress;
    public TextView mTvAccount;
    public TextView mTvHolding;
    public Button mBtTransfer;
    
    
    public TrustTransferDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_trust_transfer);
        
        getWindow().setBackgroundDrawableResource(R.color.white);
        
        mIbtClose = findViewById(R.id.ibt_close);
        mTvMobile = findViewById(R.id.tv_mobile);
        mTvAddress = findViewById(R.id.tv_address);
        mTvAccount = findViewById(R.id.tv_account);
        mTvHolding = findViewById(R.id.tv_holding);
        mBtTransfer = findViewById(R.id.bt_transfer);
        
        mIbtClose.setOnClickListener(this);
        mBtTransfer.setOnClickListener(this);
    }
    
    /**
     * @param mobile
     * @param address
     * @param account
     * @param holding
     */
    public void setParameters(String mobile, String address, String account, String holding) {
        mTvMobile.setText(mobile);
        mTvAddress.setText(address);
        mTvAccount.setText(account);
        mTvHolding.setText(holding);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibt_close:
                dismiss();
                break;
            case R.id.bt_transfer:
                mOnClickListener.transfer();
                break;
            default:
                break;
        }
    }
    
    public interface OnClickListener {
        
        void transfer();
    }
    
    private OnClickListener mOnClickListener;
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
    
    
}
