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
public class QScanResultNotAddressDialog extends Dialog implements View.OnClickListener {
    
    public ImageButton mIbtClose;
    public TextView mTvAddress;
    public Button mBtCopy;
    
    
    public QScanResultNotAddressDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT | Gravity.TOP;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_qscan_result_not_address);
        
        getWindow().setBackgroundDrawableResource(R.color.white);
        
        mIbtClose = findViewById(R.id.ibt_close);
        mTvAddress = findViewById(R.id.tv_address);
        mBtCopy = findViewById(R.id.btn_copy);
        
        mIbtClose.setOnClickListener(this);
        mBtCopy.setOnClickListener(this);
    }
    
    public void setParameters(String address) {
        mTvAddress.setText(address);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibt_close:
                dismiss();
                break;
            case R.id.btn_copy:
                dismiss();
                mOnClickListener.copy();
                break;
            default:
                break;
        }
    }
    
    public interface OnClickListener {
        void copy();
    }
    
    private OnClickListener mOnClickListener;
    
    public void setOnClickListener(OnClickListener registerClickListener) {
        this.mOnClickListener = registerClickListener;
    }
    
    
}
