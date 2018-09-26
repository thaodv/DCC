package io.wexchain.android.dcc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import io.wexchain.dcc.R;

/**
 * @author Created by Wangpeng on 2018/7/16 20:04.
 * usage:
 */
public class DeleteAddressBookDialog extends Dialog implements View.OnClickListener {
    
    public ImageView mIvWarning;
    public TextView mTvText;
    public TextView mTvTips;
    private TextView mBtCancel;
    private TextView mBtSure;
    
    private OnClickListener mOnClickListener;
    
    public DeleteAddressBookDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_delete_address_book);
        
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        mIvWarning = findViewById(R.id.iv_warning);
        mTvText = findViewById(R.id.tv_text);
        mTvTips = findViewById(R.id.tips);
        mBtCancel = findViewById(R.id.bt_cancel);
        mBtSure = findViewById(R.id.bt_sure);
        
        mBtCancel.setOnClickListener(this);
        mBtSure.setOnClickListener(this);
    }

    public void setBtnText(String sure,String cancel){
        mBtCancel.setText(cancel);
        mBtSure.setText(sure);
    }

    public void setTips(String tips){
        mTvTips.setText(tips);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                mOnClickListener.cancel();
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
    
    public void setTvText(String text) {
        mTvText.setText(text);
    }
    
    public interface OnClickListener {
        void cancel();
        
        void sure();
        
    }
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
}
