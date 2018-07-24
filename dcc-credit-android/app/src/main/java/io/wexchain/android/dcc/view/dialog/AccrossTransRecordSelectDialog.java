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

/**
 * @author Created by Wangpeng on 2018/7/16 20:04.
 * usage:
 */
public class AccrossTransRecordSelectDialog extends Dialog implements View.OnClickListener {
    
    public TextView mTvThisWeek;
    public TextView mTvThisMonth;
    public TextView mTvLatest2Month;
    public TextView mTvStartTime;
    public TextView mTvEndTime;
    public Button mBtSure;
    
    
    public AccrossTransRecordSelectDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_across_trans_record_select);
        
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        mTvThisWeek = findViewById(R.id.tv_this_week);
        mTvThisMonth = findViewById(R.id.tv_this_month);
        mTvLatest2Month = findViewById(R.id.tv_latest2month);
        mTvStartTime = findViewById(R.id.tv_start_time);
        mTvEndTime = findViewById(R.id.tv_end_time);
        mBtSure = findViewById(R.id.bt_sure);
        
        
        mTvThisWeek.setOnClickListener(this);
        mTvThisMonth.setOnClickListener(this);
        mTvLatest2Month.setOnClickListener(this);
        mTvStartTime.setOnClickListener(this);
        mTvEndTime.setOnClickListener(this);
        mBtSure.setOnClickListener(this);
        
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
            case R.id.tv_latest2month:
                mOnClickListener.latest2month();
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
        
        void latest2month();
        
        void startTime();
        
        void endTime();
        
        void sure();
        
    }
    
    private OnClickListener mOnClickListener;
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
    
    
}
