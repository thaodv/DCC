package io.wexchain.android.dcc.view.dialog.trustpocket;

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
public class TrustWithdrawDialog extends Dialog implements View.OnClickListener {
    
    public ImageButton mIbtClose;
    public TextView mTvAddress;
    public TextView mTvAccount;
    public TextView mTvFee;
    public TextView mTvToAccount;
    public TextView mTvHolding;
    public Button mBtSure;
    
    
    public TrustWithdrawDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_trust_withdraw);
        
        getWindow().setBackgroundDrawableResource(R.color.white);
        
        mIbtClose = findViewById(R.id.ibt_close);
        mTvAddress = findViewById(R.id.tv_address);
        mTvAccount = findViewById(R.id.tv_account);
        mTvFee = findViewById(R.id.tv_fee);
        mTvToAccount = findViewById(R.id.tv_toAccount);
        mTvHolding = findViewById(R.id.tv_holding);
        mBtSure = findViewById(R.id.bt_sure);
        
        mIbtClose.setOnClickListener(this);
        mBtSure.setOnClickListener(this);
        
    }
    
    /**
     * @param address
     * @param account
     * @param fee
     * @param toAccount
     * @param holding
     */
    public void setParameters(String address, String account, String fee, String toAccount, String holding) {
        mTvAddress.setText(address);
        mTvAccount.setText(account);
        mTvFee.setText(fee);
        mTvToAccount.setText(toAccount);
        mTvHolding.setText(holding);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibt_close:
                dismiss();
                break;
            case R.id.bt_sure:
                mOnClickListener.sure();
                dismiss();
                break;
            default:
                break;
        }
    }
    
    public interface OnClickListener {
        void sure();
    }
    
    private OnClickListener mOnClickListener;
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
    
    
}
