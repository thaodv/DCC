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
public class AcrossTransDialog extends Dialog implements View.OnClickListener {
    
    public ImageButton mIbtClose;
    public Button mBtSure;
    public TextView mTvTransOrientation;
    public TextView mTvMaxPayMoney;
    public TextView mTvOrderMoney;
    public TextView mTvMaxAbsenteeism;
    public TextView mTvHoldingCount;
    
    public AcrossTransDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_across_trans);
        
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        mIbtClose = findViewById(R.id.ibt_close);
        mBtSure = findViewById(R.id.bt_sure);
        mIbtClose.setOnClickListener(this);
        mBtSure.setOnClickListener(this);
        mTvTransOrientation = findViewById(R.id.tv_trans_orientation);
        mTvMaxPayMoney = findViewById(R.id.tv_max_pay_money);
        mTvOrderMoney = findViewById(R.id.tv_order_money);
        mTvMaxAbsenteeism = findViewById(R.id.tv_max_absenteeism);
        mTvHoldingCount = findViewById(R.id.tv_holding_count);
        
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibt_close:
                mOnClickListener.cancel();
                dismiss();
                break;
            case R.id.bt_sure:
                mOnClickListener.sure();
                break;
            default:
                break;
        }
    }
    
    public interface OnClickListener {
        void cancel();
        
        void sure();
        
    }
    
    private OnClickListener mOnClickListener;
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
}
