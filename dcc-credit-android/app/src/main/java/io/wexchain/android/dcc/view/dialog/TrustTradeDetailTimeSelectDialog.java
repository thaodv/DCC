package io.wexchain.android.dcc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import io.wexchain.dcc.R;
import io.wexchain.dccchainservice.util.DateUtil;

/**
 * @author Created by Wangpeng on 2018/7/16 20:04.
 * usage:
 */
public class TrustTradeDetailTimeSelectDialog extends Dialog implements View.OnClickListener {
    
    public TextView mTvThisWeek;
    public TextView mTvThisMonth;
    public TextView mTvStartTime;
    public TextView mTvEndTime;
    public Button mBtSure;
    
    public TrustTradeDetailTimeSelectDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_trust_trade_detail_time);
        
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        mTvThisWeek = findViewById(R.id.tv_this_week);
        mTvThisMonth = findViewById(R.id.tv_this_month);
        mTvStartTime = findViewById(R.id.tv_start_time);
        mTvEndTime = findViewById(R.id.tv_end_time);
        mBtSure = findViewById(R.id.bt_sure);
        
        
        mTvThisWeek.setOnClickListener(this);
        mTvThisMonth.setOnClickListener(this);
        mTvStartTime.setOnClickListener(this);
        mTvEndTime.setOnClickListener(this);
        mBtSure.setOnClickListener(this);
        
        mTvStartTime.setText(DateUtil.getCurrentMonday("yyyy/MM/dd"));
        mTvEndTime.setText(DateUtil.getCurrentSunday("yyyy/MM/dd"));
        
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_this_week:
                mOnClickListener.week();
                dismiss();
                break;
            case R.id.tv_this_month:
                mOnClickListener.month();
                dismiss();
                break;
            case R.id.tv_start_time:
                mOnClickListener.startTime();
                break;
            case R.id.tv_end_time:
                mOnClickListener.endTime();
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
        
        void week();
        
        void month();
        
        void startTime();
        
        void endTime();
        
        void sure();
        
    }
    
    private OnClickListener mOnClickListener;
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
    
    
}
