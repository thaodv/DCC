package io.wexchain.android.common.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import io.wexchain.android.common.R;

/**
 * @author Created by Wangpeng on 2018/7/16 20:04.
 * usage:
 */
public class WaitTransDialog extends Dialog implements View.OnClickListener {
    
    public ImageView mIvWarning;
    public TextView mTvText;
    public ImageButton mIbtClose;
    
    public WaitTransDialog(@NonNull Context context) {
        super(context, R.style.deleteAddressBookDialog);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER;
        getWindow().setAttributes(attributes);
        setContentView(R.layout.dialog_wait_trans);
        
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        mIvWarning = findViewById(R.id.iv_warning);
        mTvText = findViewById(R.id.tv_text);
        mIbtClose = findViewById(R.id.ibt_close);
    
        mIbtClose.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {

    }
}
