package io.wexchain.android.dcc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import io.wexchain.dcc.R;

/**
 * @author Created by Wangpeng on 2018/7/16 20:04.
 * usage:
 */
public class GetRedpacketDialog extends Dialog implements View.OnClickListener {
    
    private TextView mTvTitle;
    private ImageButton mIbtClose;
    private TextView mTvText;
    private TextView mBtCancel;
    private TextView mBtSure;
    
    private OnClickListener mOnClickListener;
    
    public GetRedpacketDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_get_redpacket);
        
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        mTvTitle = findViewById(R.id.title);
        mIbtClose = findViewById(R.id.ibt_close);
        mTvText = findViewById(R.id.tv_text);
        mBtCancel = findViewById(R.id.bt_cancel);
        mBtSure = findViewById(R.id.bt_sure);
        
        mIbtClose.setOnClickListener(this);
        mBtCancel.setOnClickListener(this);
        mBtSure.setOnClickListener(this);
    }
    
    public void setBtnText(String cancel, String sure) {
        mBtCancel.setText(cancel);
        mBtSure.setText(sure);
    }
    
    public void setTitle(String tips) {
        mTvTitle.setText(tips);
    }
    
    public void setText(String text) {
        mTvText.setText(text);
    }
    
    public void setIbtCloseVisble(int show) {
        mIbtClose.setVisibility(show);
    }
    
    public void setBtSureVisble(int show) {
        mBtSure.setVisibility(show);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibt_close:
                dismiss();
                break;
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
    
    
    public interface OnClickListener {
        void cancel();
        
        void sure();
        
    }
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
}
