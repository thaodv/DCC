package io.wexchain.android.dcc.view.dialog;

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
public class TrustTradeDetailStyleSelectDialog extends Dialog implements View.OnClickListener {
    
    public TextView mTvIn;
    public TextView mTvOut;
    public TextView mTvMyIn;
    public TextView mTvMyOut;
    public TextView mTvCancel;
    
    
    public TrustTradeDetailStyleSelectDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_trust_trade_detail_style);
        
        getWindow().setBackgroundDrawableResource(R.color.white);
        
        mTvIn = findViewById(R.id.tv_in);
        mTvOut = findViewById(R.id.tv_out);
        mTvMyIn = findViewById(R.id.tv_myin);
        mTvMyOut = findViewById(R.id.tv_myout);
        mTvCancel = findViewById(R.id.tv_cancel);
        
        
        mTvIn.setOnClickListener(this);
        mTvOut.setOnClickListener(this);
        mTvMyIn.setOnClickListener(this);
        mTvMyOut.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_in:
                mOnClickListener.in1();
                dismiss();
                break;
            case R.id.tv_out:
                mOnClickListener.out();
                dismiss();
                break;
            case R.id.tv_myin:
                mOnClickListener.myin();
                dismiss();
                break;
            case R.id.tv_myout:
                mOnClickListener.myout();
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }
    
    public interface OnClickListener {
        
        void in1();
        
        void out();
        
        void myin();
        
        void myout();
        
    }
    
    private OnClickListener mOnClickListener;
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
    
    
}
