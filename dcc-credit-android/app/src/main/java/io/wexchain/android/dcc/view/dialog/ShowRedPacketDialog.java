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
public class ShowRedPacketDialog extends Dialog implements View.OnClickListener {
    
    public TextView mTvTasks;
    public Button mBtnOk;
    
    private OnClickListener mOnClickListener;
    
    public ShowRedPacketDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_bound_wechat2);
        
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        mTvTasks = findViewById(R.id.tv_tasks);
        mBtnOk = findViewById(R.id.btn_ok);
        
        mTvTasks.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tasks:
                mOnClickListener.goToTask();
                dismiss();
                break;
            case R.id.btn_ok:
                mOnClickListener.ok();
                dismiss();
                break;
            default:
                break;
        }
    }
    
    
    public interface OnClickListener {
        void goToTask();
        
        void ok();
    }
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
}
